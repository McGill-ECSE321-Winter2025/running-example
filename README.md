# Demo Project For ECSE 321
## Overview
![image](https://github.com/user-attachments/assets/4ea12433-8f65-409b-ae59-0476bf6bf6c9)
A simple demo projet for ECSE 321 using the tech stack taught in class (Java Spring, PostgreSQL and Vue)  

Parts of the implementation are *deliberately* incomplete, insufficient or straight up **bad**, so that students can explore on their own how to implement them

> [!WARNING]
> Don't blindly copy from this demo project, that defeats the point of the class. If you don't understand what it does, you should probably try to understand it before implementing something similar in your project.  It's also obvious when your code is reviewed
## Setup
Clone the repo, and add a `.env` file in the parent directory. A sample env file is attached below
```
POSTGRES_DB=running-example
POSTGRES_USER=ray
POSTGRES_PASSWORD=ummmm

VITE_API_URL=http://localhost:8080/
```

The demo is configured to initialize the database with a set of dummy data. All accounts have a password of `password123`, the followiung 4 accounts are always created
- jessie.galasso
- ray.liu
- alex.liu
- jean.paul

> [!WARNING]
> The demo uses ports 5432 (Postgres), 8080 (Backend) and 5173 (Frontend). You may get conflicting port issues if you are running the demo project and working on your own project, be sure to kill the demo project before starting your own, or use different port numbers
  
### Using Docker (You should use this)
If you have Docker installed, it is as simple as cloning the repo and running in the parent directory
```
docker-compose up -d
```
Your backend will be available at `localhost:8080`, and your frontend at `localhost:5173`.

- If you have made changes to the data and wish to restart fresh, run `docker-compose down -v` and then `docker-compose up -d`

- To "hot-reload" the backend, run `docker-compose up -d --build backend`. You should have hot-reloading on the frontend by default

> [!NOTE]
> If you have the dockerized frontend running, and you run `npm run dev` locally (non-dockerized), it will open another instance of the frontend on a different port (usually 5174). To work around this, either kill the frontend container and rerun `npm run dev` to restart it on port 5173 or add 5174 to the permitted sources in CORS
### Using Raw Binaries
1. Ensure you have an instance of Postgres running on your system. Ensure that the `POSTGRES_USER` and `POSTGRES_PASSWORD` fields in your `.env` file matches the credentials of your Postgres instance
2. From the parent directory, navigate into the backend directory. Run the following commands:
   ```
   ./gradleW build -x test
   ./gradleW bootRun
   ```
   Your backend should be available at `localhost:8080`
4. From the parent directory, navigate into the frontend directory. Run the following commands:
   ```
   npm install
   npm run dev
   ```
   Your frontend should be available at `localhost:5173`
## Technical Stuff



## Tools Used
- IDE: [Zed](https://zed.dev/), with https://github.com/zed-extensions/java and https://github.com/zed-extensions/vue
- Browser/Frontend Tooling: [Arc](https://arc.net/)
- DB Schema Tool: [DBML](https://dbdiagram.io/d)
