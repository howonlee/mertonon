# Technical Q & A

## This can't possibly have within 10 orders of magnitude of enough data volume.

We use the semantics of internal representations, which seem to, in practice, have a phase transition where they actually get going much quicker than solid optimizational results. In either case, the optimizational results start very quickly, and a lot of the exponential increases in wanted data volumes in modern times are for very large models from which different things are asked.

We also elicit initial weights directly and locally from people, as neural nets are pretty prototypical SDIC systems and because the weights will inevitably be subject to political maneuvering, which is unfortunately condign to the underlying task. The manual judgements of people are also materially less susceptible to global gaming, even if local gaming will be omnipresent.

The ultimate notion to talk of is of _constraints_, not of _data_. Ultimately, each org can be construed as one point of data, but thats doesn't help that individual org very much, does it?

That said, the data and parameter volumes are indeed absolutely miserable and we'll probably have to endure this for some time.

## You attempt to defy Sutton's bitter lesson: you will regret this!

Frankly, I'm already regretting it. But internal financial data remains a bastion of absolute proprietariness in basically all cases for nontrivial businesses led by people who don't consider themselves ideologues. The constraint-as-opposed-to-data semantics makes things begin to work, but this will continue to remain pretty inferior to most phase-transition-dependent semantical sorts of things by mere data volume in most instances, because most instances will resolutely remain proprietary.

However, one possible counterpoint is that the basic underlying structure of the project is to spread the bitter lesson to human management, whereupon one could really easily say that it is a mere continuation of the lesson. Certainly, we intend to better AS Beer on this front.

We will take any serious computation-leveraging that is feasible as they come up. We do not disagree with the fundamental bitter lesson that the space for human knowledge in attempts at AI has resolutely proven to be nil.

## Can I use some open data set?

It is in the nature of fine-grained managerial accounting data that it is blisteringly both proprietary and political, at least emically political to the organization, and we're not aware of any trustable open data sets. We can try to start one but we don't think there'll be many takers. Mandated financial reporting in nearly all cases will not be fine-grained enough for Mertonon.

## Why not reinforcement learning?

That entails ennumerating the state space and action space and perhaps policy / value models, which would also entail making the model more domain-specific, with the current state of RL technology. Multi-task learners exist but are much more difficult, as a technology. We want to ship something that actually gets a useful job done and endure mostly product-risk, as opposed to having existential technical-risks off the bat, although there will be technical risks as things go on.

I suppose the cool peeps do RLHF nowadays. Implementations that actually exist seem pretty LM-specific, so that's another thicket of jungle to cut through if we're to do that.

## Why not like, a proper transformer like someone not from 1989?

"Ontogeny recapitulates phylogeny" is surprisingly mediocre as a statement about neonatology and surprisingly great as advice about the neonatology of software products. Transformers will come eventually, but we highly suspect that you're going to need to retool the org entirely to fit the tool in the transformer case, like a SAP installation where you rearrange the org to use SAP and do everything the SAP way but even more disruptive. There is also the possibility of schlepping the given Mertonon model inside of a larger transformer of some kind as one of the the feedforward sections, but we haven't tested this yet. As things are, people already have companies and the tool needs to fit the org. People have to go and want it first. It has to both work and have people really, really believe that it works in order for people to go do that SAP-style redo-your-whole-org sorta thing, so not really a v1 or even a v2 project.

Something more basic and similar to sigma-pi networks is very straightforward and is on the roadmap. But, again, ontology recapitulating phylogeny.

## Why not ordinary graph neural nets?

By closely interlacing the details of the model into the semantics of the net, we attack the sparsity of data problem and the permutational invariance problem that one could say led to the development of GNN somewhat more directly. We took inspiration from the so-called ["wavefunction collapse" algorithm](https://github.com/mxgmn/WaveFunctionCollapse), which doesn't actually have anything to do with quantum anything, rather the ordinary physics of condensed matter, inasmuch as it's a constraint satisfaction thing that squeezes constraints from _one_ putative data point by squeezing many constraints from that one data point. Each org is ultimately one data point, but you can squeeze quite a few constraints from that one data point, just like images in wavefunction collapse.

But instead of the usual GNN tasks of node and link classification and community detection and so on, we actually want the $\frac{\partial energy}{\partial network}$ (the delta) semantics the ordinary backpropagation gives us, because that's the optimization direction we pop out for people to use. We have the optimization target, we have inputs, and so on.

Eventually there will be some kind of GNN model tacked on for embedding calculations, completions, community detection and so on, but not at release. It seems simple enough once we have the ordinary backprop graph, to stick the node and link embedding semantics into it separately.

## Wait, all this layer stuff has a graph semantics - why don't you use a graph db?

We need to have the software install in 5 minutes with no problems on absolute potato-level boxes. Currently we require a Postgres install but eventually we will enable SQLite for the completely lazy case in which user does not even have a DB server (R. Hipp's serverless, not J. Bezos's serverless). There are no graph DB's that exist that are as simple to use and as good as Postgres or SQLite.

## Where is the classification or regression semantics?

Differentiable systems have an optimization semantics first of all, which gets translated into classification or regression domains. Mertonon uses that optimization semantics directly: organizations are optimization machines of something-or-other in Mertonon's view.

## Why roll everything neural networky yourself? Why did you write your own autodiff?

We do have some strange requirements.

1. Strong need for rigorous testing up front on both numerical and correctness issues of many kinds.
2. This whole thing is never compute-bound. We will have to run _one_ batch or minibatch consisting of some few samples _every few hours_, tops. Realistically, every few days or a week or something. Therefore, 0 need for GPU support. Barely memory-bound, neither: why _not_ literally go use swap? Why not literally abandon BLAS and LAPACK for a long while and take great big gains in simplicity from that?
3. Deranged topologies up front. Network topologies with material semantic significance. Huge amounts of sparsity. That sort of thing.
4. Serious need for numerical representations amenable to reporting without the usual monstrosities of the IEEE float (and bf16). Rational types to be used in anger.
5. Strange attitudes with respect to rounding. Other neural net libs don't have to care about materiality.
6. Serious need for reporting at all. If we encounter a trade off between model performance for reporting, _in most cases we will go for the better reporting_, which nothing that you can crack open out of a can in neural-net land cares about, for great reasons. This is a reporting thing, ultimately.
7. Strong importance of determinism. People get tetchy about anything nondeterministic going into their evaluations. There's going to be test rigs for determinism, most certainly. We don't have it yet, but we will.
8. Highly non-novel initial algorithm. We're taking the delta and noting the semantic meaning of it in the CSP underlying an organization. If you don't think about organization, this was old hat in 1988. Rest is just backpropagation, eventually general reverse-mode AD. And reverse-mode AD can, if and only if you don't care too much about performance (see #2), and don't have serious floating point problems (see #4) be implemented in a simple series of pure functions which are easy enough to debug.

## Why Clojure?

The deployment story in a JVM language is much better. Of the JVM languages, we are best with Clojure. Python is radically better on the numerical front but the deploy story is kind of a terrible mess that you basically need Docker for, if third parties are supposed to host it. It seems that generally the ML side of Python is fine with this as a continuing issue, because the researchers who numerical Python is materially targeted at don't do their own deployments, as a rule.

You can't assume Dockerization in the corporate settings we're going to gun for, but you can expect a JRE installation of some kind. You can't assume that you have the good-will of some MLOps team who knows how to deal with weird memory-bound stuff or TPU instances or whatever. We have to fit into the general blub of corporate IT somehow, and something something JVM is the best path to do this. Kotlin is fine but we don't know it well enough, Scala and Groovy and original Java itself we don't like very much.

If you want to be a strange person sitting around and replacing the programming work of a ridiculous amount of people, Clojure seems to be a good language to try that in, empirically. Borkent and Prokopov and Corfield and Cam and others manage it, at least - might as well try, even if we fail.

Other possible considerations are Julia, Matlab, C++, Fortran or full-stack JS. Matlab isn't gratis or libre, C++ and Fortran are unfortunately too filled with the ancient cruft of ages to learn specifically for the project in time (actually, Clojure was learned on-the-job previously), and Julia's Quickcheck support and JS's (and Typescript's, frankly) numeric typing are both basically a joke, unfortunately.

One differential downside of Clojure is that nontechnical people in an organization, especially management, hate it to a surprisingly similar degree as senior devs love it. Clojure is usually the most or second-most loved language in the yearly Stack Overflow survey, so we're talking a virulent, venomous and enduring hatred, for many reasons, some pretty explainable in a realpolitik sort of way. You make a project entitled "Neural Organizational Management" and a q&a section starting off, "Is this going to automate away my job?" and you're going to earn some virulent hatred anyways, so that downside doesn't matter for us.

## Why don't you just reduce everything to SAT and pull out a good solver and be done with it?

A lot of solvers doing CDCL sort of work behave in a jerky way with respect to intermediate results: that's the essence of backjumping as a strategy, after all. Also, determining soft constraints is difficult even for ordinary jobsite allocation tasks, never mind something that's intended to be used by people who aren't schooled in arcane-to-the-ordinary-person CSP tasks. There is probably a proper soft-CSP task manager that could be made somewhere, but product simplicity and relative transparency rather than raw power is the goal.

We do have a nontrivial dual between the solverier-sorts of things and ordinary function optimization that we have, but it's not a high priority to implement, and those things are actually kind of a dime a dozen in theory-land.

## Isn't this going to drive all the slack out of the budgeting system and make everything fragile?

We do predict that the shipped initial version will have a tendency to do this after 100 or so accounting periods (we advocate very short accounting periods because the gradients are themselves pretty small, so this is less time than you'd think). We have putative attempts in the roadmap to deal with this problem.

## No global optima?

We don't need global optima, we just need optima which are materially better than the status quo. L. Walras claimed a global equilibrium theory first a century ago with tatonnement but he didn't know anything about computers, having died in 1910. We are aware of the Arrow-Debreu model and don't care about it, just as the modern neural net learner doesn't care about and very often doesn't even know about the general perceptron learning results and counterexamples and counter-counterexample results.

## This optimization is path-dependent.

So it is. That's cost assignment for you. All of this is profoundly nonconvex.

## This project is worthless and you should feel bad.

Please refer to the general Q&A under the question, "I hate you personally and wish to do you symbolic harm." and follow the instructions therein.

## What's with this weird normalizing nonlinearity on both the activations and weights you have going on? Why not use a normal-people nonlinearity like ReLu or something?

1. The activation normalization is much more human-interpretable if you have someone owning an individual layer, since the normalized values can be interpreted as fractional assignments of the budget of the responsibility center. We told you about the reporting over performance thing above, right? It would be a bit of a bad budgeting tool if managers couldn't understand it, incomprehensible technical q&a's notwithstanding.
2. This is an extremely concise way to deal with vanishing and exploding gradients. Each weightset nonlinearity applied, after the normalization, has by fiat (by virtue of being a stochastic matrix) a jacobian eigenspectrum which means that they won't explode or vanish on us. So that's why there's no ordinary layernorm or batchnorm thing here. You do have to do the weird stddev scaling rigamarole, though, which is kind of alarming, especially numerically.
3. We plan to do renormalization semigroup sorts of analysis later. We skimmed the Roberts-Yaida [textbook](https://engineering.purdue.edu/DeepLearn/Resources/DeepLearningTheory.pdf) they're writing and noted that a lot of the physico-mathematical legerdemain kind of assumes you don't have controls over the functional form of the model, which we actually do. If you look closely at the cost object instead of the stuff they look at I think there's a path to a way more straightforward Kadanoff-style RG picture instead. We intend for ordinary people to understand some material qualitative parts of this thing, which is incompatible with ever showing them anything like equation $`\infty.180`$ from that book.
4. We were also piqued by the chapter of that book reminding us of the cited Saxe et al work about deep linear nets; we have often thought of the many attempts to get at "the most linear nonlinearity" which many groups have tried, and the normalization might be a nice little dark horse.
5. Gradient itself goes through fine iff scaled, not a little bit because of that stochastic matrix structure of the weightsets.
6. We also plan to do stat-mech-of-Bayes-nets sorts of analysis (like, Parisi, Mezard, Zecchina, Virasoro, those folks sort of thing - much discombobulated from their original spin-glass-cum-satisfiability point of view) later, and there's a straightforward conversion of a trained network of this kind to and from Bayes nets.

RG stuff, and specifically, RG universality and RG criticality stuff, is of great interest in putative eutrophication-based explanations for stuff. Eigenspectra problems, pretty quotidian in ordinary neural nets, are also of great interest in this domain.

It's still a nonlinearity, it's neither additive nor homogeneous. However, the normalization does have some fun little shiny properties in how it bucks additivity and homogeneity. $f(ax) = f(x)$ for nonzero a, for example.

If it just completely doesn't work, we'll splice in something more quotidian, but our generated and proprietary-data tests have shown it working well enough.

## You go on about soft constraint satisfaction problems and then you show up with a multilayer perceptron. What gives?

> Many cognitive-science problems are usefully conceptualized as constraint-satisfaction problems in which a solution is given through the satisfaction of a very large number of mutually interacting constraints. The problem is to devise a computational algorithm that is capable of efficiently implementing such a system. Connectionist systems [ed.: neural nets, as we might call them today] are ideal for implementing such a constraint satisfaction system, and the trick for getting connectionist networks to solve difficult problems is often to cast the problem as a constraint satisfaction problem. In this case we conceptualize the connectionist network as a constraint network in which each unit represents a hypothesis of some sort (for example, that a certain semantic feature, visual feature, or acoustic feature is present in the input) and in which each connection represents constraints among the hypotheses. Thus, for example, if feature B is expected to be present whenever feature A is present, there should be a positive connection from the unit corresponding to the hypothesis that A is present to the unit representing the hypothesis that B is present. Similarly if there is a constraint that whenever A is present B is expected not to be present, there should be a negative connection from A to 8. If the constraints are weak, the weights should be small. If the constraints are strong, then the weights should be large. Similarly the inputs to such a network can also be thought of as constraints. A positive input to a particular unit means that there is evidence from the outside that the relevant feature is present. A negative input means that there is evidence from the outside that the feature is not present. The stronger the input, the greater the evidence. If such a network is allowed to run, it will eventually settle into a locally optimal state in which as many as possible of the constraints are satisfied, with priority given to the strongest constraints. (Actually, these systems will find a locally best solution to this constraint satisfaction problem. Global optima are more difficult to find.) The procedure whereby such a system settles into such a state is called relaxation. We speak of the system relaxing to a solution. Thus a large class of connectionist models contains constraint satisfaction models that settle on locally optimal solutions through the process of relaxation.

> _Parallel Distributed Processing_, JL McClelland et al (chapter by DE Rumelhart)

(full disclosure: Howon was Jay McClelland's postdoc's undergrad minion back in undergrad.)

The reason to adopt this instead of something more within the ordinary remit of soft constraint satisfaction is the nature of the representation: we want a distributed representation that is somewhat durable to tiny points of missing data, we want soft semantics for the constraint satisfaction, we want something easier to define for a typical organization and easier to use.

The problem is that the well-characterized phase structure of SAT and the similarly well-characterized behavior of CSP solvers is proven for the non-soft case. But this was in large part due to the analytic tractability of proving and the decreased interest by the theorists, who like other theorists try to avoid too much grubbiness. SAT solving is pretty unique in that theorists have made quite significant contributions and have created a large base of useful theoretical knowledge while being also able to resolutely avoid basically all the grubbiness of most other industrial computation. The theory encompasses the reality fairly well, which one can't say of neural network-land.

We have a novel spectral way to represent optimization problems as something significantly more tree-searchy that we have lying around we can implement when we do it, probably after sharing it first. There are actually a lot of obscure things like this, we may do a review one of these days.
