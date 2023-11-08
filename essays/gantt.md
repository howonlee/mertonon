Using Mertonon instead of Gantt Charts for Project Planning and Estimation
---

There is a specific, grinding, bureaucratic doom that awaits many people who do things (individual contributors) in organizations. Here is the form of it. Management sits you down and asks,

"So what's your estimate for this task?"

If you're a callow youth, you give your offhand take. If you're not, you give a grimly-considered prognosis or start hemming and hawing or try to wriggle out of it like a worm caught in a bird's mouth. Either way, that's not the doom that comes for you.

"7 weeks."

"So... 7 weeks. Why couldn't it be 5?"

Doom!

The source of the doom is in mismatched expectations and worldviews, because management (management that survives) is a political animal in a way that forces the individual contributor to be. The mistake is to have the estimate at all, when the quantity that is wanted is a political judgement on effort. "Why couldn't it be 5?" is an attempt at power politics, not a statement about putative base reality.

Politics is at the core of the question of resource distribution in groups - it cannot be avoided and to avoid it is to fail at resource distribution. It can work so well that it becomes invisible, though - another failure and mistake. To never talk about power is to encode structures of resource allocation without ever being able to revise them. Even if it isn't really talk for polite company. Therefore excuse us for talking about power frankly.

Mertonon is a new way to plan and budget for orgs, for any kind of budget. With Mertonon, you make a picture of your org as a neural network. You do this by going on Mertonon and linking together local, political, human judgements of impact with respect to KPI's. These don't pretend to be objective or apolitical as many Gantt chart estimates do. Also unlike Gantt charts, Mertonon itself will suggest changes to your budget based upon those judgements.

Gantt charts are [directed acycle graph](https://en.wikipedia.org/wiki/Directed_acyclic_graph)-structured schedules, where the arrows in the network graph model dependencies. Each node in a Gantt chart (usually represnted in Gantt chart software as some kind of rectangle) is a task with estimate, the edges connecting the nodes are dependencies. A topological sort gives an ordering of what to do, and summation of the topological sort with concurrent elements done concurrently gives an overall estimate of the task length.

Like all other data structures, Gantt charts have a built-in opinion on what the resources in question _are_, and how they should be allocated for best usage. Therefore they are _political_ tools, with an opinion on what kinds of computation and which kinds of resources are being allocated. They encode a structural point of view on power which is suited to industrial work.

(If you need another example on how data structures can be _political_ in nature, consider the British person's complaint about how foreigners _cannot queue worth a damn_ - because the foreigners don't respect the point of view on resource allocation that queues represent).

The political worldview inherent in the Gantt chart has a crisp source. It's from H. Gantt creating them them to organize industrial plants, in imitation of the organization of his father V. Gantt's slave plantation, although to his credit H. Gantt at least disavowed slavery as a way to organize society.

The world is different today, one hopes for the better. In modern work, there must be political observance given to the voice of participants in the work - modern work is differentiated in that the individual worker also participates in deciding what work to do and how to do it.

This is particularly pronounced in highly technical work because management often doesn't have observability in how to do highly technical work at all, so workers often must be the whole arbiters of, or huge contributors on, how to do the work. Also with great influence on what work to do.

This is an etic factor, a factor from the external point of view, in the Gantt chart, because of its roots in tasking of individuals without individual power, but built-in to Mertonon.

- Example: RBAC.
- Mertonon will tell you where to move towards the allocations. The original purported purpose of Gantt charts is to do this indirectly - to allocate the resources from the estimate. Instead of giving the estimate and allocating from there, Mertonon suggests the allocations directly.
- What's a cost object in this situation? They're the individual tasks.
- What's an entry in this situation? The recordings of peeps actually expending effort on the tasks. Relative effort, more than hours - Mertonon will normalize.
- Weights? These are the dependencies peeps determine in the tasks, so this is the alternative to the Gantt DAG ordering. Importantly, they're also fully intended to be political indications of resource allocation.
- The difference is in the greater political mutability of Mertonon instances.
- When the weights mutate as they will because Mertonon will suggest new weights, these will indicate the structures of power and of dependency actually within the group of peeps doing the task
- This is what is meant by Mertonon giving political observance given to the voice of participants in the work

Contrast to how to do Gantt charts in software naively - this doesn't work because it's an infliction of power on people who have serious power to resist and who can benefit the organization by resisting foolish injunctions.

We intend Mertonon instances to be places where you can store unseemly discussions of power and go back to work afterwards, if you're in the kind of culture where this sort of frank discussions of power and politics are unacceptable.

We also fully intend that Mertonon's suggestions also work for the very specific quotidian purpose of looking good and blaming someone else for your failures. A guide for that and countermeasures and counter-countermeasures and counter-counter-countermeasures is forthcoming whenever Mertonon allocates my effort to it.
