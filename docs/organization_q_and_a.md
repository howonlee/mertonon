# Organization Q and A

## Is this going to automate my job away?

Did programming languages automate programmers' jobs away? Mertonon is a tool, not an agent. Mertonon puts tools in your hands to do your job better, it won't replace your job at this time.

## That's some patter I've heard a thousand times. I have power in an organization. Is this a threat to my power?

The initial phase of adopting Mertonon is in some large part the act of writing down and formalizing, in a distributed budget, informal structures of power in an organization. If you have lots of formal but no informal power, then this will reveal that you have lots of formal but no informal power in the organization - but you would've figured this out quickly anyways, from people not listening to you. In either case it would have been a revealing of things that were there, not changing.

But the actual budget recommendation system of Mertonon must have the end goal of the organization that you directly _or_ indirectly assist as a formal goal merged in order for Mertonon to not unjustly reduce your budget allocations, whether that's revenue, ESG, compliance, or what-have-you. Garbage in, garbage out. So if you do directly or indirectly assist your org's stated goals locally, and have them written down in Mertonon, you needn't worry - the global impact can be calculated by Mertonon itself.

If you're really unnerved about it, contact us directly.

## Actually, I hate being a manager. Can I automate my job away with this?

Not at this time, but supporting development might allow us to help with some shitty, hateful portions of your job, if you go and use Mertonon and buy the hosting and paid features when we have them and so on. We have listened to a lot of managers complaining about the misery of their jobs and we are always up for more if you're in the SF Bay Area sometime.

## Within this org or sub-org I'm in, everyone backstabs each other at the drop of a hat. There's no loyalty at all in some cases and insane cronyism in others. People are vicious and cutthroat about credit attribution. Overall this org or sub-org I'm in is this roiling, venomous pit of snakes. Can you do anything to help that?

According to our interviews with people, this is the premier shitty, hateful portion of the managerial job.

The answer is "soon". We plan to give this some serious feature and documentation attention. We have a putative main cause of the phenomenon. No guarantees, but this is definitely a main product direction. We have literally heard the verbatim phrase "pit of snakes" 4 or 5 times in our interviewing. We know that this is a problem and will spend serious time and attention to attack the problem.

We may give off an impression of thinking that all politics in management is ruthless cutthroat realpolitik. We know it isn't so, but the people who are satisfied with their political situations mostly don't talk to us, they get on with their lives.

## Can you do stack ranking with this?

We would really rather you didn't, because people really hate stack ranking for some pretty valid reasons, but you unfortunately can. You're on your own for doing this, sorry.

## Can I ignore your protestations about not using this to fire managers immediately and go and fire managers immediately using this?

We would really rather you didn't, because we haven't finished like 95% of this yet, but you unfortunately can. You're on your own for doing this, but if you're in the SF Bay Area we can go and get a coffee and I can hear you out and maybe try to get you to not do it, or at least get you to pay and rejigger things in the backlog after listening to you.

## When's the module for doing executive stuff coming?

One of these years. Not this year, probably, but within 10 years. 5-8, if there becomes a significant contributing community or a business is made viable. Moravec's paradox still obtains, so we can't actually say whether executive or line management will be materially impacted first.

## Where's the method to do something better than manually pulling it out of our butts for the weights? Like, email and docs integrations or something.

On the roadmap. We have a small set of prospective integrations listed in repo and a list of 200 or so integrations lying around on some paper. However, these methods will always be materially more gameable than personal judgement.

## Does Mertonon plan to integrate with... whatever?

We will always integrate with anything anyone suggests for any reason, in good faith or bad. This is a rigorous and unyielding policy with exception only for violence and partisan politics. However, we will have radical order-of-magnitude differences in prioritization of integrations and depth of integrations always. We will always integrate with anything and everything eventually, but we will prioritize our integrations depending upon arbitrary factors including popularity, whether an enterprise customer wants it, and so on, so "eventually" will span a material amount of time.

Do not ask us questions about whether we will integrate with something. Just put up an issue on Github telling us we'll do it. As long as it's nonviolent and nonpartisan we'll just accept it.

## This is obviously gameable.

We ship with two countermeasures against gaming. First is that all the weight applications are renormalized, so weight allocations are de facto percentage-based instead of being subject to arms-races of that sort. Second is that no cycles are allowed in the weight structure, so trivial methods to do self-licking ice-cream cones become more difficult.

We have put serious prototyping work into many possible countermeasures against many possible ways to game the system, and will be shipping the ones that empirically work as time goes on. We wish to remind you that the status quo of budgets are currently also gameable, we just have to run that red queen's race, forever.

## I want to be recommended specific actions to take, not the differences in credit assignment.

That's coming when we do reinforcement learning one of these days, so not very soon.

## How half-assed can I implement Mertonon? Can I do it chabuduo (差不多)?

Mertonon will still _work_ and give estimations even if the structure given of the organization is not incredibly complete, even if many (but not the biggest and most important) journal entries are entirely omitted, and even if data collection is quite slovenly (but without material, order-of-magnitude typos). Like other neural net systems, Mertonon is fairly (but not completely) durable towards missing and somewhat-corrupt data. Cleaner is better, though, of course. Also, like all other neural nets, systematic and adversarial attacks on data will futz up Mertonon instances.

We will explicitly keep chabuduo installations and chabuduo usage in mind during development.

## Does the above mean that I can half-ass the actual quantity of data?

No. Although the threshhold to get it to start working is low, there will almost certainly be the usual neural data-quantity phase transition at a ridiculous-seeming threshhold. Like all other neural technologies, begging, borrowing and stealing to get more data will inevitably work better than anything done on the software-writing side.

## Can you do cost node-only self-organization with this? (Do I need to actually decide these weights or can my reports do it?)

Yes. Eventually we will also have a guide for doing this. This isn't a manager-free flow, it's a manager-does-not-decide-which-cost-objects-in-adjacent-responsibility-centers-are-most-important-to-the-responsibility-center-and-instead-individual-contributors-do-it flow.

## Is this going to have serious litigable IP problems like ChatGPT, Copilot, DALL-E, and others?

All data used in Mertonon should be proprietary to your organization and produced by people in an employment, consulting, partnership, agentic, advisory or other affilation relation with the organization, as it's effectively budgeting data. Foundation models are fundamentally difficult in this domain.

## How is this different from ERP systems?

If you say the words "enterprise resource planning", there is an implicature of vast, unmanageable software that tries to do everything for an organization. ERP systems are also often some of the worst software that exists, due to this implicit taking-on of infinite scope. Every ERP major (SAP, Oracle, Microsoft, IFS, Infor, etc) claims up, down and sideways that they have some AI thing going on (and some IOT thing... and some blockchain thing... and some quantum computing thing... and any other word you can think of). Despite the totalizing point of view of ERP systems in what they write down and the processes they de facto impose on organizations (SAP is famously intransigent about this in particular), ERP's are also basically silent on issues of decision on allocation.

We tend to believe that ERP is a pejorative among people who actually know and like software. We won't be insulted if you call Mertonon an ERP system but we won't call it one ourselves. Mertonon differs from ERP systems inasmuch as it has a radically different point of view on the issues listed in the previous paragraph:

1. We aim to do a few things well, as opposed to being everything for everyone. That said, we do know that many people like everything-for-everyone ERP's, and that all the previous ERP's also started off thinking that they were going to aim to do a few things well.
2. It is true that we are focused on a thing which has become a buzzword, neural nets, but we are intently focused on it and we like to believe sometimes that we actually know what we're doing.
3. We will not impose specific processes on you like the ERP majors de-facto do. Our generative testing suites will make things compatible with more ways of working.
4. We are at this time fundamentally not serious with regards to financial reporting and accounting reports, the actual bread and butter of ERP. We will be, but we are not, at this time.
5. You will be able to write plugins in a better programming language than ABAP or SuiteScript or X++, that ordinary programmers also sometimes use.
6. Mertonon will always have suggestions for allocations, as opposed to merely about processes. You don't have to take the suggestions, but it will make them and they will be there.

Many ERPs in the market can also colorably claim many of these, except for the direct suggestions for allocations. Therefore, that is the main difference between Mertonon and ERP's. This reaches down to the roots of the system: your average ERP has a series of database tables at the core, Mertonon has a reverse-mode autodifferentiator in addition to a bunch of database tables.

As of this time, nearly no functionality of an ERP can be replicated on Mertonon. We really wish to focus on new functionality but there will be commercially reasonable efforts to replicate very many of the components, especially if they're useful in the allocations. We also intend to make Mertonon interoperable with common ERP's with integrations for most of them for multiple purposes.

## My ERP system that I already use swears up and down that they're at the forefront in leadership in ML and AI like every other ERP system, why would I care about this?

ERP ML/AI departments are usually tacked on after-the-fact because the genesis of most of their origins is literal decades ago. Even newer entrants to the space do not generally have the wherewithal to suggest allocations directly, preferring more conventional small ML tasks.

The autodiff system we built is at the actual core of the Mertonon system and makes suggestions for allocations: it is the difference between aiding the act of writing down the budget and suggesting the contents of the budget. You can confirm this by inspection, this is not patter.

## Give me some accounting details.

There is no proper double-entry bookkeeping at this time, this is a single-entry system only at this time. Fixing that's on the roadmap. Cash or accrual basis is not featurized and currently user must keep track of it - fixing this is also on the roadmap. You can't do zero-base anything at this time, it's incremental only at this time. We don't have income statement and balance sheet views as of yet. Again, on the roadmap. We have heavily focused on the machine-learning features before release but we know that people want the usual financial-accounting necessaries and we will get on it. Look [here](roadmap.md) for a full roadmap.

We have thought hard about reconciling absorption costing as required by GAAP to Mertonon costing, and that, too, is on the roadmap. That's not up to us, ultimately, though, obviously.

## Will my proprietary data and/or allocations be shared with anybody?

No. Ever. Mertonon is currently self-hosted: even when we do have the cloud version, we will rigorously tenant the cloud boxes so that data is not shared between instances and secure even from us. You can inspect the source to make sure that this is the case, or airgap the machines that Mertonon will run on, if you really want to make sure.

We will get a privacy policy memorializing this before we start selling the professional edition.

We will ask for anonymous usage data to help us make Mertonon better, but this is not the budgeting or allocation data in any way shape or form, and you can also inspect what it is precisely, if you'd like.

## Do you have other people's data and allocations to train on?

Unfortunately, we have promised the above question to people. As a managerial accounting tool, Mertonon deals with intensely proprietary and small data. We will attempt to get an MIT-licensed dataset together but we haven't had any takers so far.

## Our lawyers heard "partially AGPL software" and they started rocking back and forth in the corner gibbering about things that human beings were not meant to see. Will you dual-license this?

Yes, for consideration. Please contact us.

## Should I build or buy a system like this?

To replicate the allocation suggestions of Mertonon directly, you will need serious deep neural net expertise, a large amount of enterprise software expertise and a fair amount of managerial accounting expertise. None of those are cheap. Neural net expertise specifically isn't incredibly common, although it's getting more common nowadays. You are welcome to be inspired by the specific solutions to the specific model issues we encountered in making the open core of Mertonon, but you need that expertise anyways to understand our work in the first place. People who can even _read_ Clojure don't come cheap.

Eventually, when we get around to implementing the statistical physics features, you will need a physicist of condensed matter specializing in the condensed-matter-like dynamics of AI problems, or an AI specialist who knows enough condensed matter physics: there are probably not more than 1000 of those people on this planet. Perhaps 200. Not talking about the neural networks sorts of people who are easier to find, but the satisfiability and CSP sorts of people who don't get any press. You could probably find them easily enough, and stick some money in front of them, but the trouble is that they probably don't know enterprise software and don't care about organizational anything, they care about condensed matter.

That said, you can get a fair ways doing ordinary managerial accounting by simple regression analysis locally for specific issues, and that's as close as opening up your Excel installation. Mertonon is for continuous organization-wide allocation, which gets brutally complicated even at 20 people by that method. You will also be tempted to integrate the system closely with payroll, probably with some material rewrite for the occasion, which is as close to a magic recipe for inducing failure to ship as any: K. Beck would know.

We do not have specific pricing for you yet, but we fully intend to cost less than a single fully-loaded Bay Area software expert, even in nearly all enterprise edition contracts, never mind the combination of an enterprise software person, a neural net person, a condensed matter physicist and an accounting person. There will also always be a free-as-in-freedom and free-as-in-beer community edition of Mertonon that has a very material portion of the features of the full set. We are fully committed to the open core model with a large open core and always will be committed.

## Yeah, but Mertonon wasn't invented here - I don't want to deal with stuff that wasn't invented here.

If you want to make Mertonon feel like it's yours, we intend to create a plugin ecosystem which admits of very ordinary programming (as opposed to most ERP ecosystems, which make you learn some strange language and go into a sort of dried mud pool of a technology ecosystem to do anything) which will also admit of very ordinary development in Clojure or plain Java. Clojure is obscure but software people like it in good faith, which you really can't colorably say about ABAP or suchlike, and Java is of course not obscure at all. In addition, neural networks are programs in-and-of-themselves, so simple ordinary usage of Mertonon will also create programs adapted to your org. Also, we will offer whitelabelling for paid instances eventually.

## Is Mertonon suitable for small and medium-sized organization? How small a organization? How large?

We are directing development towards both the needs of small-and-medium-sized organizations and enterprises (mega-orgs), at price points and levels of hands-on and hands-off support appropriate to both types of organizations. A minimum of about 5-10 cost nodes in the books is needed for the dynamical aspects of the algorithm to work, and a transaction volume of more than 5-10 transactions per budget review period (ideally, more than 30-60 transactions a budget review period), is necessary. We recommend pretty frequent budget review periods with Mertonon. At least one intermediate (hidden) center needs to be present for Mertonon to be useful, otherwise you could rig up regression models in Excel instead.

Mertonon UX is not really suitable right now and scaling hasn't been done for organizations above around 50 people, although individual grids can split up organizations if you'd like. Unlike the minimum requirement, this maximum can be moved and will be alacritously.

## What are some technical limits on the cost nodes that Mertonon can deal with?

Currently, we have no indexing for location or temporal structures, so if your goods are highly location-dependent and shipping is required, or if they're perishable, Mertonon is not suitable at this time. The main remit of Mertonon costing is for bureaucracies and software shops initially, where labor is the main cost and the costs and revenue are difficult to attribute to the people doing the labor. Factory plant and retail budgeting is not as extremely alienated and Mertonon's attacks on the organizational economic problems will probably not be as simple and easy as the ordinary alternative of linear or logistic models in Excel. That said, recurrent (temporal) and locational models will come eventually.

## Is Mertonon suitable for competitive intelligence usage and usage for personal advantage in internal politics?

Yes, although you're going to have to guess or get some other way the weight data and much of the transaction data, so realistically Mertonon use has as prerequisite quite a lot of internal knowledge about the org already. We will have feature attention and docs for this one of these days.

## We are some small vassal team in a vast megacorp, mega-org or tech major, fairly alienated from other teams and owing ostensible fealty to a larger suzerain org that might not have our best local interests at heart. Can we use Mertonon to deal with and strategize with our relation to our suzerain and co-vassals?

Yes. We will also have a guide for this specific usage and some feature attention for it eventually. Mertonon will always be a third party to your struggles so that might help things along.

## We are a larger suzerain org in a vast megacorp, mega-org or tech major, theoretically receiving fealty from some unruly vassal org, but they do not necessarily have our best local interests at heart. Can we use Mertonon to deal with and strategize with our relation to them?

Yes. We will also have a guide for this specific usage and some feature attention for it eventually. Mertonon will always be a third party to your struggles so that might help things along.

## Is Mertonon suitable for illegal or clandestine organization usage?

As Stringer Bell said in _The Wire_, "is you takin' notes on a criminal f---n' conspiracy?"

The answer is no. I am not sure why people like asking this question, but they do, unfortunately. You often get problems with two sets of books in financial accounting, but Mertonon is at this time a managerial accounting tool only, and making counter-budgets and alternative budgets is often the substantive content of legitimate managerial politics.

## Is Mertonon suitable for price-fixing and other illegal market coordination?

See previous answer. Mertonon is for legitimate managerial accounting within single firms only.

## Do I need new software for this at all? Could I do this on a spreadsheet?

Of course, it is always possible to do budgeting and allocations manually. However, to implement something actually _similar to_ Mertonon, which suggests the actual allocations, is difficult on spreadsheets and infeasible by hand. Mertonon is materially computationally enabled: the suggestions are done by a reverse mode autodifferentiation process which is not feasible to do by hand frequently for more than about 5-10 nodes. Writing a neural network in Excel with nontrivial topologies basically comprises nontrivial software development done in Excel, which plays into the expertise needed, as mentioned in our comments on the build vs. buy decision.

Often, management in a simpler organization might be well served by linear and logistic regression methods, which can be construed as mathematically simpler relatives of neural nets and are very feasible for implementation via spreadsheet. Most managerial accounting textbooks will have a treatment on how to do such regressions semi-manually via Excel. The general difficulties of Excel development - the general lack of source control and professional software discipline, the lack of scalability, the idiosyncrasies of the languages allowed - will of course apply.
