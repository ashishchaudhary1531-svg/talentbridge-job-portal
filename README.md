# TalentBridge Job Portal

TalentBridge is a Java web application for employers, job seekers, and administrators. It is built with Jakarta Servlets and JSP, JDBC, and SQLite. It follows the Java Web project rubric and includes both the web application and a local presentation deliverable.

## Run it on Windows

1. Install a JDK 17 or newer and make sure `java` is on `PATH`.
2. Double-click `run.bat` from this folder, or open PowerShell here and run:

   ```powershell
   .\run.bat
   ```

   On first run, the included Maven bootstrap downloads Apache Maven from an Apache mirror and verifies the archive against its SHA-512 checksum. Maven then resolves the project dependencies, builds the WAR, and starts the embedded Tomcat server.
3. Open [http://127.0.0.1:8080](http://127.0.0.1:8080). Keep the console window open while using the portal. Run `stop.bat` to shut it down gracefully, or press Ctrl+C in the server console.

If port 8080 is occupied, run the launch command from PowerShell with another port:

```powershell
.\mvnw.cmd -DskipTests clean package exec:java -Dexec.mainClass=com.talentbridge.StandaloneServer -Dexec.classpathScope=provided -Dserver.port=8081
```

The default web app listens on the local computer only. Set `-Dserver.address=0.0.0.0` only if you intentionally need access from another device on your network.

## Demo accounts

The first run creates three demo accounts and sample jobs/application data. All three accounts use the password `demo1234`.

| Role | Email |
| --- | --- |
| Administrator | `admin@talentbridge.demo` |
| Employer | `employer@talentbridge.demo` |
| Job seeker | `seeker@talentbridge.demo` |

New job seekers and employers can register from the sign-in page. Administrator accounts can only be created by an administrator.

## What is implemented

- **Job seekers:** create an account, edit profile details, search and filter approved jobs, apply with a cover note and optional PDF/DOC/DOCX resume (up to 5 MB), track application status, download their resume, and message the employer.
- **Employers:** manage job postings, resubmit edits for review, close or delete postings, review candidates, update application status, download submitted resumes, and message applicants.
- **Administrators:** review pending job postings, manage user profiles/roles/access, remove accounts, update portal welcome/support settings, and view summary metrics and recent activity.
- **Shared features:** password hashing with PBKDF2, session renewal after sign-in, role checks, CSRF tokens, prepared JDBC statements, SQLite foreign keys, an activity worker, and a scheduled expired-job cleanup task.

## Project layout

```text
src/main/java/com/talentbridge/
  db/          JDBC connection and DAO classes
  model/       role-specific users, jobs, applications, messages, repository interface
  security/    password hashing
  service/     demo seed data and managed background workers
  web/         filters, context startup, servlets, and embedded Tomcat entry point
src/main/resources/schema.sql
src/main/webapp/
  WEB-INF/views/   JSP pages
  assets/          responsive CSS
prototype/         earlier static browser mockup (reference only)
```

The runtime database is created at `data/talentbridge.db`; resumes are stored in `data/uploads/`, outside the public web directory. Override them with JVM properties `-Dtalentbridge.db=...` and `-Dtalentbridge.uploads=...` when needed.

## Database outline

`users` stores the three account roles. `jobs` belongs to an employer. `applications` joins a seeker to a job and stores its current status and private resume path. `messages` belongs to an application. `saved_jobs`, `activity_log`, and `system_settings` support portal extension points and administrator operations. Foreign keys cascade when a user or job is removed; email and per-job application uniqueness are enforced by SQLite.

## Java web rubric mapping

| Review area | Implementation |
| --- | --- |
| Problem understanding and solution design | Role-specific workflows, application lifecycle, and layered MVC structure |
| Core Java | Inheritance/polymorphism for account roles; interfaces/generics in `Repository`; exceptions and validation |
| Database integration (JDBC) | `Database` and focused DAO classes use JDBC prepared statements and transactions at the persistence boundary |
| Servlets and web integration | Jakarta Servlet 6 filters and controllers, JSP views, multipart resume upload, session handling, responsive UI |
| Multithreading/concurrency extension | Managed single-thread activity worker and scheduled expiry worker with orderly shutdown |

## Build a submission WAR

```powershell
.\mvnw.cmd -DskipTests clean package
```

The WAR is generated at `target/talentbridge.war`. To deploy it to a separate Tomcat 10.1 server, use that server's `webapps` folder. The `prototype/` folder is not the portal runtime.
