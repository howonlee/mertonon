## Misc Notes (for testing)

- There's a lot of futzing around with larger generated bits in tests, because everything tags along in order to stay reversible. Try to use `clojure.data/diff` on the property tests when they break to actually get the individual elements that broke. Be real cognizant of this! Often you get this scary giant thing in there and you should really be using `clojure.data/diff`!

- The intention is to use nearly all property tests for Mertonon, to lay down indirect fire at the inevitable great tide of vermin. It's not going to be easy going if you don't know what property tests are. You can read [this article](https://fsharpforfunandprofit.com/posts/property-based-testing-2/), or read [Fred's book](https://www.amazon.com/Property-Based-Testing-PropEr-Erlang-Elixir/dp/1680506218).

- Do not write application code without having some kind of property to test it on. It can be a pretty lame property.

- That said, we aim to rely heavily on a few workhorses so we can avoid thinking too much. These are usually multiple implementations (usually one simpler, one more fiddly), encoder-decoder pairs, and a bunch of properties that should apply to all CRUD. We have the CRUD ones implemented in `test_utils` so you can just pull them out and whack them in when necessary. You can think of a surprising number of things as encoder-decoder pairs if you think wibbley enough, and doing multiple implementations is usually straightforward, inevitably trivial in performance optimization work (where there already exists a slow implementation).

- If a bug is found live, try to write a property test for regression test so we have a better chance of killing everything of that same category of bug.

- It's usually less work to write it twice and thereby have a guaranteed property than to write it once and deal with the vermintide. Easier to write code than to read it.

- Take care of the tests and the code will take care of itself. Take care of the architecture and the tests will take care of themselves. Take care of the properties and the architecture will take care of itself.
