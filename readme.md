# 🥞 Mertonon - Neural Organizational Management

<!-- ci / version spam -->

<!-- video here when we do it -->

[Mertonon](https://mertonon.com) is a neural network economic planner for organizations. Budgeting tools help you make recordings of how you allocate resources in your organization. With Mertonon, you make a picture of your organization as a neural network and Mertonon itself will suggest global resource allocations based upon your local, human, political assessments of impact with respect to key performance indicators. If you know about [Project Cybersyn / Synco](https://en.wikipedia.org/wiki/Project_Cybersyn), this is an attempted modern version suitable for capitalist or noncapitalist use by ordinary people.

[Click here for installation and setup instructions.](docs/setup.md) Be advised that Mertonon is pre-alpha software: there are material known and unknown issues and missing essential features.

Some examples of things you could say if you had a Mertonon instance:

- "Hey, Mertonon says if we increase the budget for Doohickey Dealios, competitiveness of firm on market goes up [making profit go up on the roadmap, arbitrary KPI's on the roadmap] - by the influence on our ability to make Fizzwoopers, then Bob Lobbs can do their job better, and there's your competitiveness."
- "We aren't really a cost center, even though we're evidently not a profit center - we're a hidden center, or an indirect profit center. Here it is, laid out on the Mertonon instance - we're integral to the working of the Loopzoop org, and they then deal with the Woopzoop people who then deal with sales. So you see how we're essential to the profit flow of the org here."
- "Look, Part Meepmoop #22 really seems like some random unimportant doodad but the quality advantage we have for it is structurally responsible for our org's success and the whole department knows it - here it is on Mertonon - so cost-cutting is probably a bad idea. You'll probably make enemies of Cobb, Dobbs and Hob specifically if you cut it hard without their consent - see here. Unintuitive, eh?"

More things:

- [Setup in 5 minutes](docs/setup.md), from a Java jar.
- Will start working with incredibly small numbers of transactions and with very incomplete info about your organization.
- Durable to fairly (but not totally) dirty data.
- No GPU or ASIC or whatever other thing needed.
- No LLM involved.
- [Extreme integration policy.](docs/misc/integrations_policy_and_list.md)
- All data will be proprietary to your organization forever.
- You can run it on your premises or on your own cloud box, if you obey the license terms.
- You don't need to ask any permission from anyone to set the open core portion up.
- Managed cloud hosting is coming.
- Non-capitalist specific features coming.
- Completely self-contained binary is coming.

Underlying Mertonon is a differentiable system of allocation ("neural network") modelling the organization as a soft constraint satisfaction problem. This is just as in the managerial-accounting theory of constraints, but dealing directly and flexibly with cost nodes instead of arbitrary processes and with distributed soft representations instead of narrowly scoped and hard representations.

Mertonon also has pretentions to be more than a responsibility accounting mechanism eventually, with a unique staffing tool, tools for trying to get allocated more (or less) budget as an individual cost node within a Mertonon installation, countermeasures, counter-countermeasures and counter-counter-countermeasures for same, anticipatory tools for problems that routinely come up in organizational management, novel measures enabled by the weight determinations, a new computationally driven economic theory of value, inequality and class, and some other ideas coming one of these days, pending upon the central managerial accounting being viable and organizations going and using it.

We also know a little of the computational side of constraint satisfaction, and contend that many strange and harmful economic phenomena in organizations are basically analogous to strange and harmful constraint-satisfaction problem phenomena. However, there are pretty good solutions to constraint-satisfaction problem phenomena which have not yet been applied to economic problems, which seem applicable and are on the roadmap as features. And there's more where that's coming from, because diagnosing and attacking bad behavior in constraint-satisfaction problems and neural nets takes orders of magnitude less time, sweat, blood and coordination than diagnosing and attacking bad behavior in economic agents in situ.

Mertonon is pre-alpha software. There are material known and unknown issues. Vast numbers of essential features are missing, including much reporting, tax, compliance, reconcilliation, invoicing, statements, control procedures, any double entry anything, authentication or authorization. Development is in an ongoing state, and there will almost certainly be serious deprecations despite the fact that we will take some (but not existential) efforts to avoid deprecations and suchlike. That said, we are also aiming at a tiny release cadence of every business Thursday and a big release cadence of whenever-there's-enough-for-a-big-release.

- [General Q&A](docs/general_q_and_a.md)
- [Organization Q&A](docs/organization_q_and_a.md)
- [Technical Q&A](docs/technical_q_and_a.md)
- [Due Dilligence Q&A](docs/due_dilligence_q_and_a.md)

# License

Mertonon is an open core project. Outside the `ee` directory, Mertonon is licensed under AGPL v3. Inside the `ee` directory and subdirectories, Mertonon is commercially licensed, although the source is available (the source being available does not constitute a license to use this code in the `ee` directory and subdirectories, and does not constitute a license to use the Enterprise Edition binary). See [license.md](license.md) for more details.

We will also offer enterprise licensing and support for the whole thing if and only if you sign an enterprise deal. We will sign enterprise deals when we have enough software to charge enterprise deal prices and endure enterprise sales cycle lengths.

Unless otherwise noted, all files © Howon Lee.

# Disclaimers

[Read these disclaimers before usage.](docs/disclaimers.md)

# Addendum

If you're a big, medium or small-sized muckety-muck at a union, commune, cooperative, foundation, government bureau or like organization, send me an email. I've done the enterprise sales thing many times but never one of those, so I have no idea what you want, whereas I know in my bones that enterprise folks want RBAC and SSO and SOC2 certs and that sorta thing. This project is for you, too, although I can't make rigorous promises.

I wish to encourage people who want to do something novel and/or weird economically with Mertonon. Contact us for details, although initially it may literally just be me going "Yaaay!" or something like that.

# Middlingly Large Bank Of Quotations

Clojure libraries' readmes _begin_ with the quotes, so I suppose the Clojure application's readme must _end_ with the quotes.

> We start by noting a theme that runs through the interviews with the Nobel laureates. They repeatedly observe that eminent scientists get disproportionately great credit for their contributions to science while relatively unknown scientists tend to get disproportionately little credit for comparable contributions...

> This complex pattern of the misallocation of credit for scientific work must quite evidently be described as 'the Matthew effect', for, as will be remembered, the Gospel according to St. Matthew puts it this way:

> > For unto every one that hath shall be given, and he shall have abundance: but from him that hath not shall be taken away even that which he hath.

> RK Merton, _The Matthew Effect in Science_

> It is now belatedly evident to me that I drew upon the interview and other materials of the Zuckerman study to such an extent that, clearly, the paper should have appeared under joint authorship. A sufficient sense of distributive and commutative justice requires one to recognize, however belatedly, that to write a scientific or scholarly paper is not necessarily sufficient grounds for designating oneself as its sole author.

> RK Merton, _The Matthew Effect in Science, II_ > [ed.: Zuckerman was Merton's wife.]

> An indication of the kinds of improvisations both tolerated and required may be inferred from an astute case study of two East German factories before the Wall came down in 1989. Each factory was under great pressure to meet production quotas - on which their all-important bonuses depended - in spite of old machinery, inferior raw materials, and a lack of spare parts. Under these draconian conditions, two employees were indispensable to the firm, despite their modest place in the official hierarchy. One was the jack-of-all-trades who improvised short-term solutions to keep machinery running, to correct or disguise production flaws, and to make raw materials stretch further. The second was a wheeler-dealer who located and bought or bartered for spare parts, machinery, and raw material that could not be obtained through official channels in time.

> ...

> Neither of these roles was provided for in the official table of organization, and yet the survival of the factory depended more on their skills, wisdom and experience than on those of any other employee. A key element in the centrally planned economy was underwritten, always unofficially, by metis.

> JC Scott, _Seeing Like a State_ [ed.: I would bet that JC Scott would hate this thing. I'm OK with that.]

> You can do this with anything! Absolutely anything:
>
> > Horrible Coal Inc. wants to raise money.

> > It sets up a special purpose vehicle, Hypertechnical Investments Ltd.

> > Horrible Coal issues bonds to Hypertechnical Investments.

> > Hypertechnical issues its own bonds to ESG funds: “We are just a little old investment firm, just two traders and two computers, no carbon emissions here! And our credit is very good, because we have no other liabilities and our assets are all investment-grade bonds. ‘Which investment-grade bonds,’ did you ask? Sorry, I’m not sure I heard you right, you’re breaking up. Anyway we’ll look for your check, bye!”

> M Levine, _Money Stuff_, July 12 2023 [ed.: The inclusion of this quote is for humor value only and does not indicate in any way shape or form that you should touch securities with Mertonon ever. Don't do it, folks.]

> Contrary to what we would like to believe, there is no such thing as a structureless group. Any group of people of whatever nature that comes together for any length of time for any purpose will inevitably structure itself in some fashion. The structure may be flexible; it may vary over time; it may evenly or unevenly distribute tasks, power and resources over the members of the group. But it will be formed regardless of the abilities, personalities, or intentions of the people involved. The very fact that we are individuals, with different talents, predispositions, and backgrounds makes this inevitable. Only if we refused to relate or interact on any basis whatsoever could we approximate structurelessness -- and that is not the nature of a human group.

> This means that to strive for a structureless group is as useful, and as deceptive, as to aim at an "objective" news story, "value-free" social science, or a "free" economy. A "laissez faire" group is about as realistic as a "laissez faire" society; the idea becomes a smokescreen for the strong or the lucky to establish unquestioned hegemony over others. This hegemony can be so easily established because the idea of "structurelessness" does not prevent the formation of informal structures, only formal ones. Similarly "laissez faire" philosophy did not prevent the economically powerful from establishing control over wages, prices, and distribution of goods; it only prevented the government from doing so. Thus structurelessness becomes a way of masking power, and within the women's movement is usually most strongly advocated by those who are the most powerful (whether they are conscious of their power or not). As long as the structure of the group is informal, the rules of how decisions are made are known only to a few and awareness of power is limited to those who know the rules. Those who do not know the rules and are not chosen for initiation must remain in confusion, or suffer from paranoid delusions that something is happening of which they are not quite aware.

> J Freeman, _The Tyranny of Structurelessness_

> The first [methodological pitfall] involves the problem of causal imputation, the problem of ascertaining the extent to which "consequences" may justifiably be attributed to certain actions. For example, to what extent has the recent increase in economic production in this country been due to governmental measures? To what extent may the spread of organized crime be attributed to prohibition? This ever-present difficulty of causal imputation must be solved for every empirical case which is studied.

> RK Merton, _The Unanticipated Consequences of Purposive Social Action_

# What's with the name?

Robert K. Merton coined (although, as he notes, did not collect the data for) the so-called Matthew effect, the notion that the "rich get richer and the poor get poorer", which certainly applies to allocations of cost inside of an org as well as outside of organizations.

Our hypothesis for why it seems a universal phenomenon in cost allocation is based upon formal similarities to the second-order phase transition of resource allocation as a problem qua physical graph model, which is definitely not within the remit of Merton's field. But we like reading him anyways. He also coined the terms "self-fulfilling prophecy" and "role model", and popularized the notion of _unintended consequences_.

Mertonon is not affiliated with the estate of RK Merton. We are also not interested in the work of RC Merton (RK Merton's son), who won the Rijksbank prize in economics, because RC Merton's stuff is about markets, not about organizations.

We also don't have anything to do with Mastodon, if you're confused about that.
