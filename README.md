# AOuth2 User Management API

<h3 align="justify"> This is User Management API, that allowes to manipulate Users and Roles (CRUD operations). Resources are protected by OAuth2, including Authorization Server, Resource Server and Client Service. </h3>

<hr>
<h3> Project contains 3 Microsevices: </h3>
<ol>
  <b>
  <li>Authorization Server</li>
  <li>Resource Server</li>
  <li>Client Service</li>
  </b>
</ol>

<hr>
<b>1. Authorization Server</b>
<p align="justify"> Accepts requests from the Resource owner (User) to obtain an Access Token for authorization.
  <br> <br>
  Microservice includes:
  <ul>
    <li>Spring Boot (Maven project) </li>
    <li>Spring Security OAuth2 (Authorization Server) </li>
    <li>Spring Data JPA (PostgreSQL Database for production, H2 Database for testing) </li>
    <li>Spring Unit Tests with JUnit and Mockito </li>
  </ul>
</p>
<hr>
<b>2. Resource Server</b>
<p align="justify"> Protects the User's resources and accepts requests from the Client Service. Accepts and validates the Access Token and returns the necessary resources.
  <br> <br>
  Microservice includes:
  <ul>
    <li>Spring Boot (Maven project) </li>
    <li>Spring Security OAuth2 (Resource Server) </li>
    <li>Spring Data JPA (PostgreSQL Database for production, H2 Database for testing) </li>
    <li>Flyway for Database migration </li>
    <li>Spring Unit Tests with JUnit and Mockito </li>
  </ul>
</p>
<hr>
<b>3. Client Service </b>
<p align="justify"> Requires access to protected resources. To access them, gets an Access Token from Authorization Server.
  <br> <br>
  Microservice includes:
  <ul>
    <li>Spring Boot (Maven project) </li>
    <li>Spring Security OAuth2 (Client) </li>
    <li>WebClient to communicate with the Resource Server</li>
    <li>Spring Unit Tests with JUnit and Mockito </li>
  </ul>
</p>
<hr>
<h3>Usage</h3>
To use this application the following is necessary:
  <ul>
    <li>Start Authorization Server </li>
    <li>Start Resource Server </li>
    <li>Start Client Service </li>
    <li>Try to access http://localhost:8080/api/v1/users with Username "m@mail.ru" and Password "1" </li>
  </ul>
