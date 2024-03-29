v0.0.commits - August 24 2023
----

- Initial release

v0.1.commits - August 31 2023
----

- Added a lot of backend stuff in preparation for adding user/password auth
- - Some migrations for user and separate user/password and session table
- - User model added
- - Login endpoint that will authn but doesn't have any actual authz linked to it yet
- Fixed frontend weight addition cost object nodes needing a second click
- h/t to M. Reinhard for finding a db spec problem on my end

v0.2.commits - September 7 2023
----

- Stared at the usage readme a lot and I'm not really satisfied with it. Multiple bad iterations

v0.3.commits - September 14 2023
----

- Dockerfile doesn't not exist anymore
- Authn and authz exists and works for backend iff you turn the feature flag :auth on. Not usable at this time. Utterly trivial authz only. Username-password authn only.
- Outlining the independent economic theory of value
- Futzing with the intro FE bit, which isn't done yet

v0.4.commits - September 21 2023
----

- Validations in the backend now
- Intro backend endpoint now actually works
- Intro frontend? also actually works to create a user w/ password
- Logins work to create sessions
- Authz does not actually work yet so you cannot colorably say that auth works in any way yet still
- JSON middleware, which to be hipster (actually for serialization reasons) we do differently from the common ring.json middleware

v0.5.commits - September 28 2023
----

- Vendorized fontawesome.
- Get app state in tests to be within the test transaction that I use to avoid spilling db state in tests
- Logging out
- Authn exists now. No authz. One admin account only.
- Begin the journey back to re-frame
- Error screen exists now

v0.6.commits - October 5 2023
---

- Current user view in the navbar
- Admin view in the navbar, the changes might be jank
- Refactor views to re-frame
- All links are by re-frame event dispatches
- Kebab-casefy API namespace
- Start on create and delete refactor to re-frame. This entails some regression on validation to be fixed asap

v0.7.commits - October 12 2023
---

- Finished refactoring to re-frame
- Infinite pile of jankiness wrt redirect after creating, deleting stuff should be gone now
- Put in separate button for the heavier validations for grad calculations
- Weight value input now nonlinear (~ O(n^2))
- BE work for updating and actual creation of logins

v0.8.commits - October 19 2023
---

- Actual creation of password logins in addition to the accounts. No serious validation on password quality yet
- Password digests handling not terrible anymore
- Add the first trivial editing of grids - rest to come when it comes
- Whack test bugs stemming from bad generation
- Whack demo bugs stemming from re-frame refactor

v0.9.commits - October 26 2023
---

- Changing FE for all the other things
- DAG selection for the event thing on FE
- Test work on FE tests

v0.10.commits - November 2 2023
---

- Material progress on FE generation in preparation for the tests
- Decided on the unfortunately jank test running method of sticking hands in the repl and running them
- nav-sidebar event exists now
- Back button for sidebar exists now
- Fixed problem with gstring.format being used wrong

v0.11.commits - November 9 2023
---

- Whack problem with the sidebar history not working after max history is bypassed

v0.12.commits - November 30 2023
---

- Change title of main page
- Drafts of estimation essay

v0.13.commits - December 7 2023
---

- Estimation essay, complete rewrite
- Rudimentary presentation
- Fold the website into the main repo
- Made website responsive
- Contact form on website

v0.14.commits - December 14 2023
---

- Static website generation actually generates
- Estimation essay, published
