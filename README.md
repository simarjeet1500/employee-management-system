# Employee Management System (EMS)

A Java Web Application implementing **Login Authentication, JDBC CRUD, Pagination + Sorting, and Email Notifications (JavaMail)** for the Advance Java case study.

## Tech Stack
- **Server:** Apache Tomcat **11** (Jakarta EE 10 → uses `jakarta.*` packages)
- **Language:** Java 17+
- **DB:** MySQL via **XAMPP** (database name: `clg`)
- **UI:** JSP + Servlets + plain CSS (no framework dependency)
- **Mail:** Jakarta Mail (Angus Mail implementation)

---

## 1. Database Setup (XAMPP)

1. Start **MySQL** from XAMPP Control Panel.
2. Open **phpMyAdmin** → click **Import** tab.
3. Choose the file **`sql/clg.sql`** included in this project and click **Go**.
4. This will create the `clg` database with two tables (`users`, `employees`) and seed data.

> Default DB credentials used in `DBUtil.java`: host `localhost`, port `3306`, user `root`, password *(blank)*. Change them in `src/com/ems/util/DBUtil.java` if your XAMPP is different.

### Demo Login Accounts
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Employee | `john` | `john123` |
| Employee | `priya` | `priya123` |
| Employee | `rahul` | `rahul123` |

---

## 2. Importing into Eclipse

1. Unzip `EmployeeManagementSystem.zip`.
2. In Eclipse: **File → Import → General → Existing Projects into Workspace**.
3. Select the unzipped folder. Click **Finish**.
4. You should see the project under **Project Explorer** as a Dynamic Web Project.

### Set up Tomcat 11
1. **Window → Preferences → Server → Runtime Environments → Add…**
2. Select **Apache Tomcat v11.0** → point to your local Tomcat 11 install dir → **Finish**.
3. Right-click project → **Properties → Targeted Runtimes** → tick **Apache Tomcat v11.0** → **Apply**.
4. Right-click project → **Properties → Project Facets** → ensure **Java 17** and **Dynamic Web Module 6.0** are selected.

> If Eclipse shows red-X build errors on `jakarta.servlet.*`, just verify step 3 (Targeted Runtime). Tomcat 11 ships these APIs.

---

## 3. Required JAR Libraries

Place these JARs inside **`WebContent/WEB-INF/lib/`** (already pre-bundled in this zip):

| JAR | Purpose |
|-----|---------|
| `mysql-connector-j-9.x.x.jar` | MySQL JDBC driver |
| `jakarta.mail-2.1.x.jar` | Jakarta Mail API + Angus impl |
| `jakarta.activation-2.1.x.jar` | Required by Jakarta Mail |

If any are missing/outdated, download from Maven Central:
- MySQL Connector/J → https://dev.mysql.com/downloads/connector/j/
- Angus Mail → https://eclipse-ee4j.github.io/angus-mail/
- Jakarta Activation → https://eclipse-ee4j.github.io/jaf/

> **Servlet/JSP APIs are provided by Tomcat 11 — do NOT add them to lib/**.

---

## 4. Configure JavaMail (Optional)

By default, email is disabled with placeholders. To enable real email sending:

Open `src/com/ems/util/EmailUtil.java` and edit:
```java
private static final String SMTP_USER     = "your-email@gmail.com";
private static final String SMTP_PASSWORD = "your-app-password";  // Gmail App Password
```

> For Gmail, generate an **App Password** at https://myaccount.google.com/apppasswords (2FA must be enabled).

If you skip this, **CRUD continues to work** — emails are simply logged to the Tomcat console as skipped.

---

## 5. Run the Project

1. Right-click project → **Run As → Run on Server** → choose **Tomcat v11.0**.
2. Browser opens: <http://localhost:8080/EmployeeManagementSystem/>
3. You will land on the **Login page**.
4. Login as `admin / admin123` to see the **Admin Dashboard** with pagination, sorting, search, and CRUD.
5. Login as `john / john123` to see the **Employee Profile** view.

---

## 6. Feature Mapping → Case Study Lab Questions

| Lab Question | Where implemented |
|--------------|-------------------|
| **1. Login Authentication + Role-based redirect** | `LoginServlet.java`, `UserDAO.authenticate()`, `AuthFilter.java` |
| **2. CRUD with JDBC (Add/Update/View/Delete) + validation** | `EmployeeFormServlet.java`, `EmployeeListServlet.java`, `EmployeeDAO.java` |
| **3. Pagination + Sorting (Name / Department / Salary)** | `EmployeeDAO.findPage()`, `dashboard.jsp` (sort links, page links) |
| **4. Email Notification on create/update** | `EmailUtil.java` (JavaMail), called from `EmployeeFormServlet.doPost()` |

---

## 7. Project Structure

```
EmployeeManagementSystem/
├── .project / .classpath / .settings/       <- Eclipse metadata
├── src/com/ems/
│   ├── util/        DBUtil.java, EmailUtil.java
│   ├── model/       User.java, Employee.java
│   ├── dao/         UserDAO.java, EmployeeDAO.java
│   ├── filter/      AuthFilter.java
│   └── servlet/     LoginServlet, LogoutServlet,
│                    EmployeeListServlet, EmployeeFormServlet,
│                    EmployeeProfileServlet
├── WebContent/
│   ├── WEB-INF/
│   │   ├── web.xml
│   │   └── lib/                <- JDBC + JavaMail JARs
│   ├── css/style.css
│   ├── admin/  dashboard.jsp, employee-form.jsp, employee-view.jsp
│   ├── employee/  profile.jsp
│   ├── login.jsp, error.jsp, index.jsp
└── sql/clg.sql                 <- run this in phpMyAdmin
```

---

## 8. Troubleshooting

| Issue | Fix |
|-------|-----|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Add `mysql-connector-j-*.jar` into `WebContent/WEB-INF/lib/` |
| Login always says "Invalid credentials" | Confirm `sql/clg.sql` ran successfully and `users` table has rows |
| 404 on `/login` | Check Tomcat console — project must deploy as context `/EmployeeManagementSystem` |
| Compilation errors on `jakarta.servlet.*` | Set **Targeted Runtimes → Apache Tomcat v11.0** (Properties → Targeted Runtimes) |
| Tomcat 11 won't accept project | Make sure **Project Facets → Dynamic Web Module = 6.0** and **Java = 17** |

---

Built for the **Advance Java** course case study.
