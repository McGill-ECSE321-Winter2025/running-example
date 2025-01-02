# Demo Project For ECSE 321
## Overview
![Image 5088x3502](https://github.com/user-attachments/assets/138e08da-3db1-4b76-8e25-fdc013421139)
A simple demo projet for ECSE 321 using the tech stack taught in class (Java Spring, PostgreSQL and Vue)  

Parts of the implementation are *deliberately* incomplete or insufficient, so that you can explore on your own how to implement them

> [!IMPORTANT]
> The goal of this class is for you to learn how to design and implement a full-stack application, copying from the demo is **detrimental** to your learning

> ECSE 321 teaches you some of the most industry relevant material in the entire SE curriculum. Taking the time to make a high quality project and understand the concepts taught in class **will** get you hired, trust me - Ray

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
> If you have the containerized frontend running and you run `npm run dev` locally, it will open another instance of the frontend on a different port (usually 5174). The non-containerized frontend will work, but will be unable to connect to the backend due to CORS. To work around this, either kill the frontend container and rerun `npm run dev` to restart it on port 5173 or add 5174 to the permitted sources in [CORS](ecse-321-backend/src/main/java/com/example/ecse_321_demo_backend/config/CorsConfig.java).
>
> Read more on CORS [here]

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
### Class Diagram
<img width="995" alt="Screenshot 2025-01-01 at 7 26 59 PM" src="https://github.com/user-attachments/assets/f63a6a88-723d-4042-bd44-4940f776f54b" />

The demo uses a simple class diagram that demonstrates the most commonly used JPA patterns you may seen in your project
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

> In a production environment this issue is exacerbated, since exceptions are often logged into Observability services such as ELK and APM to help you debug issues. Imagine trying to debug a critical service issue and having nothing but `IllegalArgumentException` everywhere in your logs

To fix this, we can *refine* our exceptions by defining our own which provide more context to what caused the issue. Our previous 3 `IllegalArgumentExceptions` can become:
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
>It is not possible to annotate on built-in exceptions. You can choose to wrap them around a custom exception type, or look into `@ControllerAdvice` and `@ExceptionHandler` [here]

Semantically, this also means that we **do not** need to have `try/catch` blocks in our Controller layers. Spring sees that our Service layer has returned an exception which has been annotated with all the information it needs to build an appropriate response back to the client

> [!NOTE]
> Beware that this is an extra level of abstraction that you may find undesireable (it is not immediately apparent in the Controller layer that the Service layer call may cause an exceptions)


#### Middleware
Perhaps the most cryptic package of the backend, middleware is a concept in API design that will follow you *everywhere*. 


Consider the common business patter of having certain resources on the database whose access we want to be "protected" Ex. Only the event creator should be able to view the registrations to that event. How should we implement this?






#### Requests & Responses
These are essentially DTOs you see in the tutorial except more granularly defined
- Requests are objects you expect to receive at your endpoints (`@RequestBody`'s)
- Responses are objects you want to return to your callers, serialized into JSONs

> I find that grouping everything under a single DTO package to get cluttered really quickly
> 
> For example, the incoming DTO for Event will most definitely not have the id field, while you may want your outgoing DTO to have it. You will have to somehow differentiate within that single package which one is which
>
> You can choose to do this through the file names `EventRequestDTO.java` and `EventResponseDTO.java` (ugly imo), or make a subpackage for Events specifically `dto/events/requestDTO.java` and `dto/events/responseDTO.java` (unergonomic)

Decide as a team which solution you prefer the most

#### Other
I use lombok to generate boilerplate, Record classes are also a suitable alternative. You can always choose to write them out manually too (or even have IntelliJ do it for you!). Again, decide as a team what you prefer 

## Tools Used
- IDE: [Zed](https://zed.dev/), with https://github.com/zed-extensions/java and https://github.com/zed-extensions/vue
- Browser/Frontend Tooling: [Arc](https://arc.net/)
- DB Schema Tool: [DBML](https://dbdiagram.io/d)
