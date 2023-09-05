Accounting is the act of recording economic facts pertaining to organizations. There is financial accounting and managerial accounting. Financial accounting is for other people (investors, the government, etc) and therefore is tightly regulated. Managerial accounting is for internal usage and therefore people have wide latitude in their managerial reporting.

Be advised that Mertonon is not financial reporting as of this time, Mertonon is only a managerial accounting system at this time. Read [all the other disclaimers](disclaimers.md) before using.

Mertonon differs from most accounting systems because there is a computationally enabled allocation portion. That is, given a picture of the organization as you enter it and the local judgements you give it, Mertonon will suggest allocations of resources within your organization via neural network backpropagation.

Therefore it is not paradoxical to say that Mertonon is a neural network system but not an AI system. There is a [neural differentiation bit stuck in there](../src/mertonon/autodiff/grad.clj), but the weights are materially determined by human beings like yourself.

---

# Using Mertonon

From the outside, organizations look like imposing edifices, united as one in one purpose towards producing hammocks or safeguarding fish or writing software or what-have-you.

But you know better, don't you? From the inside, I have found almost every organization riven by systems of homage, of fealty, vassalage - and of feud. This is the invisible organization which Mertonon seeks to make legible and align towares the ostensible goal of the organization.

You have setup Mertonon as detailed in [these instructions](setup.md). You go to the home page.

![](https://mertonon.com/assets/homepage.png)

---

You see the demo, which you can look at at your leisure. Look at the listing of _grids_. A _grid_ delimits a coherent singular cost flow which may impinge upon multiple Mertonon-style responsibility centers. Create a grid.

![](https://mertonon.com/assets/grid_create.gif)

---

Usually the notion of responsibility center encompasses a business unit which has its own goals and such, so the Mertonon conception of responsibility centers, which may be pretty alienated from an end goal but still have its local contributions attributed to the end goal, may not mesh with your organization's idea of responsibility centers. In which case, call them layers, because that's what they are in neural network lingo. You note a few - Mertonon will start working with only a few.

![](https://mertonon.com/assets/layer_create.gif)

---

A cost node is like a cost object, but because of the political nature of Mertonon we also want them to correspond to individuals in addition to ordinary cost objects. A complete picture is not necessary, although of course it helps quite a bit, especially if there is to be argumentation, which there probably will be.

![](https://mertonon.com/assets/cobj_create.gif)

---

A weightset is a grouping of weights. Weights are like in neural networks - a weighting of how a cost node impinges upon another cost node. Weights are currently arbitrary at this time and there always has to be an arbitrary fallback as response to gaming. We anticipate that much of the constraints and politics in organizations can be written down in terms of weight structures. Create some weightsets and have all each of the individual people who own the cost objects put weights in them.

![](https://mertonon.com/assets/weightset_create.gif)
![](https://mertonon.com/assets/weight_create.gif)

---

Inputs and goals are annotations on layers (responsibility centers) indicating that they correspond to costs (inputs) and resulting outputs (goals). Only these layers have journal entries in them, the internal layers have cost nodes and weights impinging upon them only.

![](https://mertonon.com/assets/input_create.gif)

---

Entries are journal entries, which are entered with respect to a cost object within an input or a goal layer. Currently journal entries in single entry with abstract notions of value are the only type supported at this time. Invoicing, actual currency, double entry, financial reporting, audit stuff, and lots of other stuff is coming.

![](https://mertonon.com/assets/entry_create.gif)

---

So, with the general picture of the organization as a neural net concluded, you can kick off the gradient and current allocations for the budget period are calculated. If there's any stuff missing and we thought of it it'll tell you.

![](https://mertonon.com/assets/kickoff.gif)

---

Then you reify the budget allocations by working differently with people based upon the weight allocation suggestions and then changing the weights if you manage to actually do so.

---

Mertonon is not written by a vast faceless corporation. If you have any problems, suggestions or anything of that nature post something in the [github forum](https://github.com/howonlee/mertonon/discussions) or the [issue tracker](https://github.com/howonlee/mertonon/issues/) and we'll talk to you.

We will eventually ship operationalizations of our theory of value, inequality and class into Mertonon. You don't have to believe any of it to use Mertonon and we will always keep an option to use this base Mertonon functionality.
