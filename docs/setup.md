# Installing Mertonon

Mertonon is built and packaged as a JAR file. You can run it anywhere Java is available.

Currently installation is a fair tad technical: we intend to make it materially less technical over time.

## Preliminaries

In order to run Mertonon on your system, you will need:

1. A running installation of Postgres on your system or on some system. Here are the [official docs](https://www.postgresql.org/download/) for how to install. If you don't like the heavyweight nature of client-server DB, SQLite implementation is coming. You probably shouldn't use a running production system, Mertonon has no substantive security at this time.
2. A running Java runtime environment (JRE) on your system. We recommend the [latest LTS version from Temurin](https://adoptium.net/).

## Installing

### Make sure Mertonon can talk to postgres

1. Set the `MT_DB_HOST` environment variable to the Postgres host. The setting defaults to localhost.
2. Set the `MT_DB_PORT` environment variable to the Postgres host. The setting defaults to port 5432.
3. [Create a Postgres database](https://www.postgresql.org/docs/current/manage-ag-createdb.html) for Mertonon, then set the `MT_DB_NAME` environment variable to the name of that Postgres database name. The setting defaults to `mertonon`. Mertonon sets its own Postgres schema (as opposed to database) at this time.
4. Note: Mertonon handles its own migrations but that means that Postgres DDL statements will be issued.
5. [Create a Postgres user](https://www.postgresql.org/docs/current/sql-createuser.html) for Mertonon, then set the `MT_DB_USER` environment variable to the username and the `MT_DB_PASS` environment variable to the password. The setting defaults to `postgres` and no password.

### Install Mertonon

1. [Download the JAR file for Mertonon Community Edition](https://github.com/howonlee/mertonon/releases/)
2. Create a new directory and move the Mertonon JAR into it.
3. Change into that new directory and run the JAR with `java --jar` then the mertonon JAR file.
4. Mertonon will log things as it starts up. Wait to see "Mertonon initialization finished!"
5. Navigate to http://localhost:5036.
6. Now you can go and poke at Mertonon. [Here are some instructions as to how.](usage.md)

### Other notes

If you have trouble installing or if you get some big ol' error message or something, post something in the [github forum](https://github.com/howonlee/mertonon/discussions) and we'll talk to you.

If you want to change the serving hostname, change the `MT_HOST` environment variable. If you want to change the serving port change the `MT_PORT` environment variable.

Paid SaaS hosting is coming.
