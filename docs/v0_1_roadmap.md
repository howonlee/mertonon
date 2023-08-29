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

- [ ] Do session table better - uuid col instead of session\_id, maybe a text col for session thing in preparation for cachingier bit
- [ ] Session model
- [ ] Session generation, joined to user/password...
- [ ] User api - route, endpoint, see if it's anything more than that
- [ ] User/password api - route, endpoint, see if it's anything more than that. creation endpoint, creates sessions, check the password with join
- [ ] User api test, User/password api test

- [ ] Some kind of middlingly durable settings db thing... maybe with table
- [ ] Testing - add the new tables to api tests
- [ ] Testing - fill out the properties thought of for password
- [ ] Testing - fill out the properties thought of for session

- [ ] Dev diary
- [ ] Changelog
- [ ] Manual clicking around a bit
- [ ] Cut release
