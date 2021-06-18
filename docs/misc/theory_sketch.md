# Incoherent ramblings towards a theory of this crap

OK, so credit allocation in economies as SAT. Two parts:

1. Arguments + evidence for why it would be
2. Followons

Usually you get the ol' cavalcade of putative causes in power law land. These get pretty domain specific, usual attack is Gibrat land in economics. But my favored putative cause is SAT stuff because there's no, like, computational aspect in Gibrat land. It's not even the behavioral vs. classical thing where peeps make decisions with limited compute or not, it's the difference between something deeply data-ish and something deeply code-ish, where we flit around that boundary like parens weenies.

Establishment of crisp phase transition can be analytic in XORSAT land, but in actual normal peeps land we're doing cavity method. Or replica trick, I don't actually crisply know yet. Cavity method is Bethe-Peierls ansatz (belief prop) with shit in it. I think we can squeeze a replica symmetry somewhere but I might be wrong.

Given the usual second order phase transition proved, you can't so much as wipe your ass without finding a power law. The favored activity of P. Bak, lol. But given that it's for a computational CSP dealio, you know the mere extension of the problem is kicking you (that intermediate stages have a character which is kicking you).

XORSAT is in P. (XORSAT has all the phase stuff but you can ignore it by fiat by Gaussian elimination, which fucked Deolalikar over) Obviously normal SAT is the most NP thing going. Where's the difference lies in the individual entries, because there's like a big ol' piece of the phase space which actually is in P, confusingly. And you can slice the inextricably difficult portions of the phase space finer with survey-prop-style stuff. NP-completeness is predicated upon instance - that's why P?=NP can't have a relativizing proof.

That hyperbolic expectation stuff might work. Might not. In either case I think with the weirdo normalization we have a path to the combinatoric thingies with the arcane walksat arguments or with the simpler moment methods

The peeps who touch SAT for a living knew this empirically, but annoyingly I don't think they talked w/ the relativization peeps - look at that Gomes Selman stuff. Achlioptas had a dealio with them iirc. But the peeps who actually touch SAT for keeps just put on random restarts. Compare to the weird Dodo bird verdict nature of revolutions, compare with primary succession in ecology (putative primary succession is a centuries-old ecology fight, I don't really know the best citations for the current state of the fight anymore)

Things in NP take a cache. They always do, in the same way as you can always find a phase transition, because you can reduce the phase transition along w/ NP-completeness reduction (and you can prolly reduce the "they take a cache" property). Maybe we can think of random restart as cache clearance, cuz they're not formally too different. There's your notion of class - and given the nature of class structure, you can immediately see that cache eviction is absolutely fucked in economic systems

Value as distributed representation of computation of resource allocations

1. The economic problem goes like this: some facts must conform to some constraints. Sometimes soft, sometimes hard.
2. You don't know the facts up front and you don't know the constraints eithewr - _very_ soft CSP.
3. Also you have to be compatible with both high finance and finance done by peeps who are literally high
4. Value exists as distributed PDP-style representation of this problem
5. Like all distributed representations of NP-complete problems, the representation matters viciously and getting it wrong will fuck you, computationally
6. You can fold all the rest of em up in this one, you ain't getting more general than "some facts have to conform to constraints, and you don't know either the facts or the contraints"

Inequality from 2nd order phase transition of NP-complete problem of price

1. Gibrat doesn't have computation but empirical systems do. Cavity method or replica thing attacks phase transition. NPC Reduction simple given tantonnement or other models of tantonnement for orgs, those damn Chicago peeps did one. Dodo bird verdict on revolutions.
2. But they don't know shit about NP-complete stuff, especially the phase transition nature and how that second order phase transition fucks you. It fucks you different in DPLL-likes (CDCL-likes) and backprop-likes but it does fuck you.

Class as cache.

1. what the fuck is a cache anyways
2. whats class and whys it a soft online anytime distributed cache for credit allocation, seems to have caching behaviors. notion of cache hit itself, notion of cache miss. this is why it's called mertonon, because rk merton's matthew effect is basically a soft distributed caching effect.
3. "lets have a cache without eviction algo" or "lets have a cache with an entity-motivated eviction algo" are obviously stupid if you put it that way, and slowdown should be expected because you fucked up on eviction
4. this is a basically deranged domain of eviction algos. 6 hour latency? fine. slightly bad accuracy? burn to death. we won't start on this for a while.
