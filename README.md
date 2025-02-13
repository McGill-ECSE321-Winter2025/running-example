# Demo Project For ECSE 321
## Overview
![Image 5088x3502](https://github.com/user-attachments/assets/138e08da-3db1-4b76-8e25-fdc013421139)
A simple demo projet for ECSE 321 using the tech stack taught in class (Java Spring, PostgreSQL and Vue)  

Parts of the implementation are *deliberately* incomplete or insufficient, so that you can explore on your own how to implement them

> [!IMPORTANT]
> The goal of this class is for you to learn how to design and implement a full-stack application, copying from the demo is **detrimental** to your learning
>
> There are many links that lead you to external resources or reading that you may find beneficial as well

> ECSE 321 teaches you some of the most industry relevant material in the entire SE curriculum. Taking the time to make a high quality project and understand the concepts taught in class **will** get you hired, trust me

## Setup
Clone the repo, and add a `.env` file in the parent directory. A sample env file is attached below
```
POSTGRES_DB=running-example
POSTGRES_USER=ray
POSTGRES_PASSWORD=ummmm

PGADMIN_DEFAULT_EMAIL=admin@gmail.com
PGADMIN_DEFAULT_PASSWORD=password

VITE_API_URL=http://localhost:8080/
```

The demo is configured to initialize the database with a set of dummy data. All accounts have a password of `password123`, the following 4 accounts are always created
- jessie.galasso
- ray.liu
- alex.liu
- jean.paul

> [!WARNING]
> The demo uses ports 5432 (Postgres), 5050 (PGAdmin), 8080 (Backend) and 5173 (Frontend). You may get conflicting port issues if you are running the demo project and working on your own project, be sure to kill the demo project before starting your own, or use different port numbers
  
### Using Docker (Recommended)
If you have Docker installed, it is as simple as cloning the repo and running in the parent directory
```
docker-compose up -d
```
Your backend will be available at `localhost:8080`, and your frontend at `localhost:5173`. 

If you require, there is also a database visualizer bundled (something similar to Mongo Compass) at `localhost:5050`. Login using the credentials defined in your `.env`, and connect to your database thru the interface

- If you have made changes to the data and wish to restart fresh, run `docker-compose down -v` and then `docker-compose up -d`

- To "hot-reload" the backend, run `docker-compose up -d --build backend`. You have hot-reloading on the frontend by default

> [!NOTE]
> If you have the containerized frontend running and you run `npm run dev` locally, it will open another instance of the frontend on a different port (usually 5174). The non-containerized frontend will work, but will be unable to connect to the backend due to CORS. To work around this, either kill the frontend container and rerun `npm run dev` to restart it on port 5173 or add 5174 to the permitted sources in [CORS](ecse-321-demo-backend/src/main/java/com/example/ecse_321_demo_backend/config/CorsConfig.java)
>
> Read more on CORS [here](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)

### Using Raw Binaries
1. Ensure you have an instance of Postgres running on your system. Ensure that the `POSTGRES_USER` and `POSTGRES_PASSWORD` fields in your `.env` file matches the credentials of your Postgres instance. 
2. From the parent directory, navigate into the backend directory. Run the following commands:
   ```
   ./gradlew build -x test
   ./gradlew bootRun
   ```
   Your backend should be available at `localhost:8080`
4. From the parent directory, navigate into the frontend directory. Run the following commands:
   ```
   npm install
   npm run dev
   ```
   Your frontend should be available at `localhost:5173`

## Technical Stuff and Notes
### Relational Schema
<img width="995" alt="Screenshot 2025-01-01 at 7 26 59 PM" src="https://github.com/user-attachments/assets/f63a6a88-723d-4042-bd44-4940f776f54b" />

The demo uses a simple relational schema that demonstrates the most commonly used JPA patterns you may seen in your project
- Single level inheritance on Events using the single table strategy
- OneToMany and ManyToOne annotations on Events and Users
- Composite primary key on Registrations
- Association class between Events and Users thru Registrations
  
### Backend
The backend follows what you see in tutorials with a few variations which are explained below

#### Exceptions
Quick refresher on exceptions in Java: Exceptions are **recoverable** runtime errors that we can handle using `try/catch` blocks similar to JS or Python.

The most common exception we see is probably the `IllegalArgumentException`, but what does it mean to have an "illegal argument"? 
- Did I provide an ID that doesn't exist in the database?
- Did I provide a syntatically incorrect username or password?
- Did I attempt to make an account with an already taken username?

The point is: *throwing `IllegalArgumentException` everywhere causes us to lose context*, it becomes almost meaningless if **every** error is caused by it.

> In a production environment this issue is exacerbated, since exceptions are often logged into observability services such as [ELK](https://www.elastic.co/guide/en/observability/current/observability-introduction.html) (Elastic, Logstash, Kibana) to help you debug issues. Imagine trying to debug a critical service issue and having nothing but `IllegalArgumentException` everywhere in your logs

To fix this, we can *refine* our exceptions by defining our own which provide more context to what caused the issue. Our previous 3 `IllegalArgumentException`s can become:
```java
//you can also use this built-in type for the first one
import javax.persistence.EntityNotFoundException;
public class EventNotFoundException extends RuntimeException{}

//honestly this is probably the most "illegal argument exception" one there is
public class UsernameFormatException extends RuntimeException{}

public class UsernameTakenException extends RuntimeException{
  //You must implement the parent constructor
  public UsernameTakenException(String username) {
        super("Username '" + username + "' already in use");
  }
}
```
Now, we have **inherently have** more context as to what each exception actually is, which we can further elaborate on in the constructor message. 

>[!TIP]
> As much as it is important to have relatively narrow scopes for each exception, if you have like 25 different exceptions, you are probably refining them too much and should consider the DRY principle
>
>For example, you don't need a "not found" exception for every possible resource. Define a single "not found" execption and elaborate on which resource wasn't found in the constructor message

In addition, we can even *annotate* these exceptions with our desired response status to our clients if we wish to return this error state

```java
package com.example.ecse_321_demo_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Username already exists")
public class UsernameTakenException extends RuntimeException {

    public UsernameTakenException(String username) {
        super("Username '" + username + "' already in use");
    }
}
```

>[!TIP]
>It is not possible to annotate on built-in exceptions. You can choose to wrap them around a custom exception type, or look into `@ControllerAdvice` and `@ExceptionHandler` [here](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html) and [here](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html)

Semantically, this also means that we **do not** need to have `try/catch` blocks in our Controller layers. Spring sees that our Service layer has returned an exception which has been annotated with all the information it needs to build an appropriate response back to the client

> [!NOTE]
> Beware that this is an extra level of abstraction that you may find undesireable (it is not immediately apparent in the Controller layer that the Service layer call may cause an exceptions)

#### Middleware
Perhaps the most cryptic package of the backend, middleware is a concept in API design that will follow you *everywhere*. It is used in loggers, authentication, rate limiting, etc. It is not a Java exclusive concept like exceptions are, and can extend far beyond just API design. While the definition varies on the context, Sonnet's definition serves us well here

> Middleware in a REST API acts as intermediary software that processes requests and responses between the client and the core application logic. It's like a chain of checkpoints that a request must pass through before reaching its destination endpoint, and that the response passes through on its way back

>[!NOTE]
> The [CorsFilter](ecse-321-demo-backend/src/main/java/com/example/ecse_321_demo_backend/config/CorsConfig.java) mentioned above is actually a piece of middleware 

Consider the common business pattern of having certain resources on the database whose access we want to be "protected". For example, only the event creator should be able to view the registrations to that event

A naive implementation would be to check in every "protected" service method if the requester is authorized to access the resources

```java
//some protected method
//we check for auth
try {
  //this can throw a cast error
  UUID userId = UUID.fromString(request.getHeader("User-Id")); 
  User user = userRepo.findById(userId)
    .orElseThrow(() -> new Exception("didn't find you");
} catch (Exception e) {
  //...
}

//do whatever you need to do after
```

There are several issues with this implementation: 
1. Every protected method will have this block of code at the top, clearly violating the DRY principle
2. Any change to this auth logic must be copied over to **every** method that requires it
3. It is not apparent in the Controller layer which endpoints are protected. Remember that any business logic should reside in the Service layer, therefore it is impossible to tell from the Controller layer which endpoints may require auth
4. While likely irrelevant to your project, consider the performance penalty of "unsuccessful" requests. Why should we call the Service layer if we know this request will not be successful?

With middleware, we can solve all these issues at once. Let's walk through the implementation of the `@RequireUser` middleware and see how

1. We only want certain routes to be protected. We distinguish them by creating a custom annotation
>[!NOTE]
> Middleware is usually applied upon **every** request. Here we restrict its application using an annotation, but you can also restrict it to certain path patterns while registering it. See the documentation [here](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/InterceptorRegistration.html)
```java
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireUser {}
```

2. We want to move the authentication logic out of the Service methods and apply it to any Controller endpoint tagged with `@RequireUser`. We create a piece of middleware by implementing the `HandlerInterceptor` interface
>[!NOTE]
> Middleware in Springboot is implemented through the `Filter` or `HandlerInterceptor` interfaces. Read more on filters [here](https://docs.spring.io/spring-framework/reference/web/webmvc/filters.html#page-title) and interceptors [here](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/handlermapping-interceptor.html#page-title) and pick what is most suitable for your needs

```java
@Component
public class UserAuthInterceptor implements HandlerInterceptor {}
```

3. We want to pass down the UserAccount to any subsequent method on the request chain. We create a UserContext object to hold this for us, and autowire it wherever we need it for easy access
```java
@Component
//Each request gets a new UserContext that is dropped when it is completed
@RequestScope 
@Getter
@Setter
public class UserContext {
    private UserAccount currentUser;
}
```

4. We want our piece of middleware to run **before** the methods it is annotated on, we implement our auth logic within the `preHandle` method from the interface
```java
@Override
public boolean preHandle(...) {
  // If the handler isn't a method (like a resource request), let it through
  if (!(handler instanceof HandlerMethod)) {
    return true;
  }

  HandlerMethod handlerMethod = (HandlerMethod) handler;

  // Look for @RequireUser annotation on the method
  RequireUser requireUser = handlerMethod.getMethodAnnotation(RequireUser.class);

  // If not found on method, check if it's on the class
  if (requireUser == null) {
    requireUser = handlerMethod.getBeanType().getAnnotation(RequireUser.class);
  }

  // If @RequireUser annotation is found anywhere
  if (requireUser != null) {
    // Get the User-Id from request headers
    String userIdHeader = request.getHeader("User-Id");

    if (userIdHeader == null) {
      throw new UnauthedException("No User-Id header provided");
    }

    try {
      UUID userId = UUID.fromString(userIdHeader);

      UserAccount user = userAccountRepository
        .findById(userId)
        .orElseThrow(() -> new UnauthedException("User not found"));

      // Store the user in the context for this request
      userContext.setCurrentUser(user);
    } catch (IllegalArgumentException e) {
      throw new UnauthedException("Invalid User-Id format");
    }
  }
  return true;  // Allow the request to proceed
}
```

There we go! We have made a piece of reusable middleware that can be "applied" to any Controller layer method or class by simply tagging it with `@RequireUser`. The full implementation is [here](ecse-321-demo-backend/src/main/java/com/example/ecse_321_demo_backend/middleware)
- We have centralized the auth logic into one location, solving issues 1 and 2
- It is immediately apparent in the Controller layer which endpoint is protected due to the annotation, solving issue 3
- Any request to a protected endpoint that does not comply with our auth requirements is immediately rejected, solving issue 4

>[!CAUTION]
>You will have this business logic pattern appear in your project. You ***DEFINITELY should not*** copy this implementation. This demo project stores credentials in plain text (!!!) and uses the ID field of a user as their "proof of authentication" which is definitely not secure. You should implement the standard JWT for authentication and use the built in `SpringSecurity` (which is middleware itself) to solve this problem. Documentation for both of these can be found @ [SpringSecurity](https://docs.spring.io/spring-security/reference/servlet/getting-started.html) and [JWT](https://jwt.io/introduction)

This example should be enough to convince you of usefulness of middleware, you should implement at least one piece of middleware in your project such as `SpringSecurity` or a basic logger (really useful for debugging when you start your frontend) as practice

#### Requests & Responses
These are essentially DTOs you see in the tutorial except more granularly defined
- Requests are objects you expect to receive at your endpoints (`@RequestBody`'s)
- Responses are objects you want to return to your callers, serialized into JSONs

> Grouping everything under a single DTO package tends to get cluttered really quickly
> 
> For example, the incoming DTO for Event will most definitely not have the id field, while you may want your outgoing DTO to have it. You will have to somehow differentiate within that single package which one is which
>
> You can choose to do this through the file names `EventRequestDTO.java` and `EventResponseDTO.java` (ugly imo), or make a subpackage for Events specifically `dto/events/requestDTO.java` and `dto/events/responseDTO.java` (unergonomic)

Decide as a team which solution you prefer the most

#### Other
Lombok is used to generate boilerplate code, but [Record classes](https://docs.oracle.com/en/java/javase/17/language/records.html) are also a suitable alternative. You can always choose to write them out manually too (or have IntelliJ do it for you!). Again, decide as a team what you prefer 

### Frontend
> [!NOTE]
> I am not a frontend engineer, I've never written production grade frontend code and generally stay far away from frontend work. I will only provide tips and some things I find helpful or useful while writing a frontend

#### General Approach
It is helpful to mock up your frontend before you get started. It helps your team decide on how you want your frontend to look and ensure you are not duplicating work by doing similar UI components

- Extensive designs using something like Figma is probably a waste of your time if you are unable to convert the time you invest into your designs into usable code. Tools for this exist but I have **never** gotten them to work properly
- Simple mockups on an iPad or [Excalidraw](https://excalidraw.com/) are more than enough for what you will do in this project

>[!TIP]
> The most important aspect of a UI is its *consistency*, a consistently "bad" looking UI is much better looking than a UI with half "bad" elements and half "good" elements. The reason why iOS or Instagram is so pleasing to use is that everything blends together seamlessly and looks exactly the same no matter what page you are on (try to find a non-rounded element in iOS). ***A consistent looking UI is a good UI***

1. Sketch out the pages you anticipate needing (translate your use case diagrams into pages), decide how you want use cases to be implemented. For example some considerations may be:
   - Should logging in/signing up be its own page? Or should it be in a popover? Maybe a modal?
   - How should events be displayed? A table or on a calendar? Or maybe both?
   - How should the flow of creating of event be handled? Dedicated page? Popover? Modal? Drawer?
3. Identify common components that can be reused across pages (such as the titlebar in the demo)
4. Build your components "bottom-up", as in go from your most component up to your page. For example in the demo:
   - Complete the filter button, new event button, menubar, table wrapper and login modal
   - Complete the titlebar, composing it with filter and/or new event buttons depending on the page
   - Implement a page by composing a menubar, titlebar and table wrapper together. Then implement the actual table
  > Given more time the DataTable would probably be its own component with the expansion rows being the only thing that changes across views

>[!TIP]
>Don't attempt to reinvent the wheel and build your own components (such as buttons, selectors, etc.), use a component library. Several are linked for you [below](#libraries--resources), but feel free to search around 

#### Vue & Frameworks
Your prof might give you the freedom to choose what frontend framework you can use, but Vue will be the one we prefer you to use

There are only maybe 4 "real" choices for frontend frameworks for you to choose from if you want them to be applicable to industry: 
- [React](https://react.dev/) is by far the most popular of the 4, however it has an extremely steep learning curve with concepts like JSX/TSX, state management and component lifecycles that is overwhelming for begineers. React is meant for building complex apps that demand high levels of user interactivity (such as Instagram or Facebook) which your project is **not**
> If the majority of your team has not written React before, do not use React. You will not see the benefits of React in your project and spend more time learning React than actually writing a frontend

- [Vue](https://vuejs.org/) is the 2nd most popular frontend framework and is much more beginner friendly as it only uses basic HTML, CSS and JavaScript. The documentation is *very good* and provides more advanced functionality that is opt-in as opposed to React which requires understanding of all functionality. Vue is used by Facebook (lol), Apple, Google and even ChatGPT
> Vue is easy to pick up and you can start writing functional frontend code on day 1, and it offers more than enough functionality to build pretty much anything you want. Unless you have a good reason to not use Vue, you should stick with this (Your TAs will likely only be able to help you with Vue)

- [Angular](https://angular.dev/) is a "batteries-included" framework, as in it bundles various libraries for common use cases (such as form management) wheras React and Vue do not. If React and Vue are "microframeworks", Angular is a "macroframework".  It has a steep learning curve, with mandatory TypeScript and understanding of RxJS. Angular is popular in enterprise applications (for internal use), but is gradually being replaced by one of the other frameworks in this list
> Don't do your frontend in Angular, feel to search online why you shouldn't (there are too many reasons to list here)

- [Svelte](https://svelte.dev/) is the newest of the bunch and is syntatically similar to Vue. While the previous 3 frameworks are V-DOM frameworks (they do most of their rendering work in the browser), Svelte is *compiled* down to pure JS, and does not ship to the browser with a runtime, resulting in a much more performant experience and smaller bundle size. Svelte is used by Apple, Spotify, New York Times and is rapidly gaining popularity.
> Svelte lacks in third party documentation and libraries compared to the other 3 options.  However, it is easy to learn and has excellent first party documentation. If you already have experience in frontend development and are looking to try something new, take a look at Svelte (*This is not officially endorsed by the profs*)

>[!NOTE]
>The tutorial notes are using the *options* API, the demo is written using the *composition* API. Read more on the composition API [here](https://vuejs.org/guide/extras/composition-api-faq.html)
>
>You should use the composition API, it is functionally superior to the options API and more in line with other frameworks

## Libraries & Resources
These are only suggestions, use whatever you prefer
### Backend
Your Spring Starter should be standardized for everyone in the class, use what your TA tells you to use. 
- Boilerplate code generation: [Lombok](https://projectlombok.org/) (lombok is finnicky and requires certain plugins to work properly, use at your own risk)
  ```
  plugins {
    id 'io.freefair.lombok' version '8.11'
  }
  implementation 'org.projectlombok:lombok'
  ```
- Common Utilities: [Apache Commons Utils](https://commons.apache.org/proper/commons-lang/)
  ```
  implementation 'org.apache.commons:commons-lang3:3.14.0'
  ```
- Authentication: [JWT](https://github.com/auth0/java-jwt)
  ```
  implementation 'com.auth0:java-jwt:4.4.0'
  ```
- Dynamic filters/queries: [CriteriaBuilder](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/persist/persistence-criteria/persistence-criteria.html) (Documentation for this kind of sucks) it is already bundled in Jakarta EE
  ```java
  package jakarta.persistence.criteria;
  ```
- Logging: [SLF4J](https://www.slf4j.org/), use SLF4J with whatever logging framework you like the most (`java.util.logging`, [logback](https://logback.qos.ch/) or [log4j2](https://logging.apache.org/log4j/2.x/index.html))
  ```
  implementation 'org.slf4j:slf4j-api:2.0.16'

  package java.util.logging;
  implementation 'ch.qos.logback:logback-core:1.5.15'
  implementation 'org.apache.logging.log4j:log4j-core:2.24.3'
  ```

### Frontend (Assuming Vue)
Use `npm` as your pacakge manager unless you have a good reason to use something else

Use `vite` as your build tool, again unless you have a good reason to use something else. ***DO NOT USE NUXT***
- HTTP requests: [axios](https://axios-http.com/docs/intro)
  ```
  npm install axios
  ```
- Component library (used in demo): [PrimeVue, Icons and Forms](https://primevue.org/) There are many small "bugs" or caveats with this library, look at their github if there is an error you can't figure out
  ```
  npm install primevue @primevue/themes
  npm install primeicons
  npm install @primevue/forms
  ```
- Component library: [ShadCN (for Vue)](https://www.shadcn-vue.com/) This is a React Port. This is not available as a package, follow instructions [here](https://www.shadcn-vue.com/docs/installation/vite.html)
- Component library: [RadixUI (for Vue)](https://www.radix-vue.com/) This is a React Port.
  ```
  npm add radix-vue
  ```
- Component library: [Element Plus](https://element-plus.org/en-US/) This seems to be Chinese (Vue is pretty popular in China), it seems to have a free Figma UI kit if that interests you
  ```
  npm install element-plus
  ```
- CSS Styling: [Tailwind](https://tailwindcss.com/) Specific guide for Vite + Vue [here](https://tailwindcss.com/docs/guides/vite#vue)
  
## Tools Used
- IDE: [Zed](https://zed.dev/), with https://github.com/zed-extensions/java and https://github.com/zed-extensions/vue 
- Containerization: [Orbstack](https://orbstack.dev)
- Browser/Frontend Tooling: [Arc](https://arc.net/)
- DB Schema Tool: [DBML](https://dbdiagram.io/d)
