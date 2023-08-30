- [x] User gen (no authz, just authn) - [here](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
- [x] User impl (model)
- [x] Delineate net table vs nonnet in registry, adjust tests
- [x] Gradient service test hang

- [x] the turbulent net pic thing, lol

- [x] User validations - caps insensitivity, uniqueness. get the test to gobble it up
- [x] User validations test
- [x] User/password banlist
- [x] Testing - figure out properties for real for user/password
- [x] User/password auth generation, joined to user gen

- [x] Do session table better - uuid col instead of session\_id, maybe a text col for session thing in preparation for cachingier bit
- [x] Session model + generate
- [x] Session model test
- [x] User api - route, test, see if it's anything more than that
- [x] Password login CRUD api (so not logging in, which creates a session, just the CRUD) - route, test, see if it's anything more than that

- [ ] Session creation api - route, endpoint, creation endpoint, creates sessions, check the password with join - doesnt want to be w the other api tests because we create only
- [ ] Yakshave ring session until it does something
- [ ] Session creation tests
- [ ] Testing - fill out the properties thought of for password
- [ ] Whack the double-click-for-weight problem
- [ ] Sketch out what to do wrt BE validations

- [ ] Contact list
- [ ] Dev diary
- [ ] Changelog
- [ ] Manual clicking around a bit
- [ ] Cut release
