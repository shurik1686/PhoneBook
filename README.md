# PhoneBook
WEB Phone book for Asterisk with function call.

The program is developed on stack: Java, Spring Boot, Vaadin, Hibernate, DB H2 and MySQL, log4j.
The phone book stores phone numbers in a short format for internal communication of employees, landlines and cell phones.
As information, additional data is added about the company's city, location of its email address, and the subscriber's position.
The call is made by sending a GET request to the Asterisk server. To determine which server to send a request to in the application, a setting has been added to the application.properties file with the phonebook.asterisk key that relates the client subnet to the Asterisk server. Example of filling in: phonebook. asteriskip=172.16.2 /172.16.2.100;172.16.3/172.16.2.100 where 172.16.2 is the subnet and 172.16.2.100 is the ip address of the Asterisk server.
When accessing the application the client's ip address is determined based on the subnet selects the necessary server address stored in the project properties file.
To work correctly, users must be configured in Asterisk.
Adding and editing the phone book is available to a user with the Administrator position. Additionally, the option to call the mail client to prepare an email for the selected subscriber has been added. By default, the application starts with a database H2 in menory additional configuration is needed. To start up permanently, you must change the properties and pom settings.
Address where the app is available http://localhost:8085/book/
