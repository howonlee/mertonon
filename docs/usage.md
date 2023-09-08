Accounting is the act of recording economic facts pertaining to organizations. There is financial accounting and managerial accounting. Financial accounting is for other people (investors, the government, etc) and therefore is tightly regulated. Managerial accounting is for internal usage and therefore people have wide latitude in their managerial reporting.

Be advised that Mertonon is not financial reporting as of this time, Mertonon is only a managerial accounting system at this time. Read [all the other disclaimers](disclaimers.md) before using.

---

# Using Mertonon

From the outside, organizations look like imposing edifices, united as one in one purpose towards producing hammocks or safeguarding fish or writing software or what-have-you.

But you know better, don't you? From the inside, I myself have found every organization without exception riven by systems of homage, of fealty, of personal politics - and of feud. Sometimes the overall melange works - and in working, turns invisible - sometimes it does not. This is the invisible organization which Mertonon seeks to make legible and align towards the ostensible goal of the organization.

This is what we mean by the allocation of resources. Mertonon is software for doing politics with regards to an organization's own internal politics, inasmuch as use of Mertonon constitutes:

1. Writing down your current political situation in Mertonon as the structure of a neural network
2. Determining the goals of the overal organization and fiddling with the settings a bit (when we have them)
3. Having Mertonon guide you towards overall predetermined goals. Including normative statements about the path your political situation should go.

You have setup Mertonon as detailed in [these instructions](setup.md). Go to the home page.

![](https://mertonon.com/assets/homepage.png)

---

You see the demo, which you can look at at your leisure. You look at the listing of _grids_. A _grid_ is a political picture of an organization or a small section of an organization that still is big enough to have divisions of labor within it and overall goals - and political structures within them, formal or informal.

That picture is in the form of a neural network. Most usages of neural networks are for getting them to represent a function, which gets then used for whatever, but here we _represent your organization_ with a neural network.

You create a grid. You work at DealioCorp - it's a small software firm, the political world which it encompasses has one current overall goal, so it probably fits in one grid.

![](https://mertonon.com/assets/grid_create.gif)

---

One of the inevitabilities of life in the modern organization is the heights of alienation that one can get up to. At companies, the salesperson talks directly to the customer - but you aren't the salesperson. You aren't even the person who talks to the salesperson to find out what the customer wants. You are the person who talks to the person who talks to the person who talks to.... the person who talks to the salepserson. Procurement folks talk directly to vendors, too, but you aren't one of them.

Sometimes the accountants formalize this structure of alienation by putting people in functional responsibility centers. The salesperson belongs in the Sales responsibility center - they talk to the Product responsibility center. Those Product folks then in turn talk to the Design (and the Design folks don't talk to Sales directly). Many times responsibility centers mean something different, many times organizations are sliced up in different ways, but in Mertonon they always mean this, so we also call them layers, because if you know neural net lingo they are also layers in the neural net.

You don't do sales. You don't do procurement, neither. You just deal with Dealios all day. (What are Dealios? No, you would need to have a 25-minute explanation to actually tell people what Dealios are, you always say at parties you Work In Software and people nod solemnly and the conversation passes to other things...)

So that's why there has to be a Dealio _layer_. Create one.

![](https://mertonon.com/assets/layer_create.gif)

---

But you don't deal with all the Dealios at DealioCorp, do you? You do Doohickey Dealios. You're the one in the company everyone talks to when they think about Doohickey Dealios. You take care of other things (other cost nodes, in our lingo) in other layers sometimes but Doohickey Dealios is what you're concerned about today, so you need to be able to allocate resources to them - which is why you create a cost node. Gotta think about those Doohickey Dealios - the necessity for them, their relation to other necessary things in the organization.

![](https://mertonon.com/assets/cobj_create.gif)

---

And now we come to the actual political meat of things. The weights.

Doohickey Dealios do need the Wibbles. After negotiation with the owner of the Wibbles node, you agreed that this is a fact, and decide to put that fact in Mertonon so folks can see it - and Mertonon can see it, and factor it into the allocations. This is a soft linkage - the stronger the necessity, the higher the value entered (Mertonon constrains the weight values to be positive currently).

![](https://mertonon.com/assets/weightset_create.gif)
![](https://mertonon.com/assets/weight_create.gif)

---

whats an input, loss
heres an example
link to why we give a shit
why we give a shit

![](https://mertonon.com/assets/input_create.gif)

---

whats an entry
heres an example
link to why we give a shit
why we give a shit

![](https://mertonon.com/assets/entry_create.gif)

---

whats the kickoff
why we give a shit

![](https://mertonon.com/assets/kickoff.gif)

Given such a political point of view, after kicking off the gradient, Mertonon will suggest allocations for cost nodes like Doohickey Dealios and new points of view on the weightings everyone should have.

---

Mertonon is not written by a vast faceless corporation. If you have any problems, suggestions or anything of that nature post something in the [github forum](https://github.com/howonlee/mertonon/discussions) or the [issue tracker](https://github.com/howonlee/mertonon/issues/) and we'll talk to you.

We will eventually ship operationalizations of our theory of value, inequality and class into Mertonon. You don't have to believe any of it to use Mertonon and we will always keep an option to use this base Mertonon functionality.
