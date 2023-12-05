Accounting is the act of writing down economic facts about organizations. There is financial accounting and managerial accounting. Financial accounting is for other people (investors, the government, etc) and therefore is tightly regulated. Managerial accounting is for internal usage. Therefore people have wide latitude in their managerial reporting.

Be advised that Mertonon is not financial reporting as of this time. Mertonon is only a managerial accounting system at this time. Read [all the other disclaimers](disclaimers.md) before using.

---

# Using Mertonon

From the outside, organizations look imposing. They look faceless and united as one in one purpose towards producing hammocks or safeguarding fish or writing software or what-have-you.

But you know better, don't you? From the inside, I myself have found every org without exception riven by political systems - riven by fealty, by feud, and other very human things. Sometimes the politics works - and in working, turns invisible. Sometimes it does not.

This is the invisible org which Mertonon seeks to make visible and align towards the stated goals of the org. "The purpose of a system is what it does". That is, to do things with system, you need to understand the system's behavior. Mertonon is software for understanding and dealing with political systems in orgs. Using Mertonon involves

1. Writing down your current political situation in Mertonon as the structure of a neural network
2. Finding out the putative goals of the overall org and fiddling with the settings a bit (when we have them)
3. Having Mertonon guide you towards putative goals by measuring the existing system's drift from them, and telling you where to change things politically in order to reduce that drift.

You have setup Mertonon as detailed in [these instructions](setup.md). Go to the home page.

Mertonon will prompt you to create an account. This is the initial admin account. Choose a good password. We will have SSO and RBAC and suchlike in some time.

![](https://mertonon.com/assets/intro.gif)

---

Having created an account, go to the homepage.

![](https://mertonon.com/assets/homepage.png)

---

You see the demo, which you can look at at your leisure. You look at the listing of _grids_. A _grid_ is a political picture of an org or a small section of an org that still is big enough to have divisions of labor within it and overall goals. A _grid_ has political structures within them, formal or informal.

That picture, in Mertonon, is drawn in the form of a neural network. Most usages of neural networks are for getting them to represent a function, which gets then used for whatever. In Mertonon we _represent your org_ with a neural network.

You create a grid. You work at DealioCorp - it's a small software firm. The political world which it encompasses has one current overall goal. It probably fits in one grid.

![](https://mertonon.com/assets/grid_create.gif)

---

One of the sad things about life in the modern org is the heights of alienation that one can get up to. At companies, the salesperson talks directly to the customer - but you aren't the salesperson. You aren't even the person who talks to the salesperson to find out what the customer wants. You are the person who talks to the person who talks to the person who talks to.... the person who talks to the salepserson. Procurement folks talk directly to vendors, too, but you aren't one of them.

Sometimes the accountants write down this structure of alienation by putting people in functional responsibility centers. The salesperson belongs in the Sales responsibility center - they talk to the Product responsibility center. Those Product folks then in turn talk to the Design (and the Design folks don't talk to Sales directly).

Many times responsibility centers mean something different, many times orgs are sliced up in different ways, but in Mertonon they always mean this, so we also call them layers, because if you know neural net lingo they are also layers in the neural net.

You don't do sales. You don't do procurement, neither. You just deal with Dealios all day. (What are Dealios? No, you would need to have a 25-minute explanation to actually tell people what Dealios are, you always say at parties you Work In Software and people nod solemnly and the conversation passes to other things...)

So that's why there has to be a Dealio _layer_. Create one.

![](https://mertonon.com/assets/layer_create.gif)

---

But you don't deal with all the Dealios at DealioCorp, do you? You do Doohickey Dealios. You're the one in the company everyone talks to when they think about Doohickey Dealios. You take care of other things (other cost nodes, in our lingo) in other layers sometimes but Doohickey Dealios is what you're concerned about today, so you need to be able to allocate resources to them - which is why you create a cost node. Gotta think about those Doohickey Dealios - the necessity for them, their relation to other necessary things in the org.

![](https://mertonon.com/assets/cobj_create.gif)

---

And now we come to the actual political meat of things. The weights.

Doohickey Dealios do need the Wibbles. After meeting with the owner of the Wibbles node, you agreed that this is a fact, and decide to put that fact in Mertonon so folks can see it - and Mertonon can see it. So Mertonon can factor it into the allocations. This is a soft linkage - the stronger the necessity, the higher the value entered (Mertonon constrains the weight values to be positive currently). Mertonon will normalize the weights to sum to 100%.

You are annoyed at those Mertonon devs for making everything fit in one number for one weight for one relation between cost nodes. The Mertonon devs wish to note that we're going to allow hypergraph semantics where there's more than one number and notes and time series and all sorts of stuff but it'll take time.

![](https://mertonon.com/assets/weightset_create.gif)
![](https://mertonon.com/assets/weight_create.gif)

---

Currently Mertonon can only aim for overall grid goals which formally correspond to conformance (formally speaking., conformity to outside conditions). We will have others in time.

To get recommendations from Mertonon, you have to point out to it one layer that corresponds to inputs to the org's value flow (cost flow) and one layer that corresponds to outputs or goals.

![](https://mertonon.com/assets/input_create.gif)

---

For those recommendations, you also put down accounting general ledger journal entries (single entry only at this time). List the transactions ascribed to the cost nodes corresponding to inputs and goals of the org. Put down the transactions corresponding to sales, materials purchases, and so on. Mertonon will then attribute value to the intermediate layers in the value flow.

![](https://mertonon.com/assets/entry_create.gif)

---

Mertonon does not make gradient and contribution determinations after every operation at this time. You have to kick them off manually. Here is how to kick them off.

![](https://mertonon.com/assets/kickoff.gif)

Given such a political point of view, after kicking off the gradient, Mertonon will suggest allocations for cost nodes like Doohickey Dealios and new points of view on the weightings to more closely correspond to the ostensible goals.

---

Mertonon is not written by a vast faceless corporation. If you have any problems, suggestions or anything of that nature post something in the [github forum](https://github.com/howonlee/mertonon/discussions) or the [issue tracker](https://github.com/howonlee/mertonon/issues/) and we'll talk to you.

We will eventually ship software corresponding to our theories of value, of inequality and of class into Mertonon. You don't have to believe any of it to use Mertonon. We will always keep an option to use this base Mertonon functionality.
