Using Mertonon instead of Gantt charts for project planning and estimation - Why and How
---

### lamo ai pic of gothic ass pictures or like press gangs

There is a concrete, grinding, bureaucratic doom that awaits many people who do things (individual contributors) in organizations. Here is the form of it. Management sits you down and asks,

"So what's your estimate for this task?"

If you're a callow youth, you give your offhand take. If you're not, you give a grimly-considered prognosis or start hemming and hawing or try to wriggle out of it like a worm caught in a bird's mouth. Either way, that's not the doom that comes for you.

"7 weeks."

"So... 7 weeks. Why couldn't it be 5?"

Doom!

The source of the doom is in mismatched expectations and worldviews, because management (management that survives) is a political animal in a way that forces the individual contributor to be. The mistake is to have the estimate at all, when the quantity that is wanted is a political judgement on effort. "Why couldn't it be 5?" is an attempt at power politics, not a statement about putative base reality.

Politics is at the core of the question of resource distribution in groups - it cannot be avoided and to avoid it is to fail at resource distribution. "Bob is reliable, Cobb is less so - put Bob on it" is a political decision. "Let's allocate more money into this, we need these Beepboopers to be better" is a political decision. Just as "Why couldn't it be 5?" is a fundamentally political question.

Domains, decisions and issues which are putatively apolitical are really indicative of politics working so well that it becomes invisible. But to keep politics always invisible is a failure and mistake. To never talk about power is to encode structures of resource allocation without ever being able to revise them. Therefore, I recognize that it really isn't talk for polite company but excuse me for talking about power and politics frankly.

## Gantt Charts

## gantt chart picture

Gantt charts are time budgets based upon [directed acycle graph](https://en.wikipedia.org/wiki/Directed_acyclic_graph)-structured schedules, where the arrows in the network graph model dependencies. Each node in a Gantt chart (usually represnted in Gantt chart software as some kind of rectangle) is a task with estimate, the edges connecting the nodes are dependencies. Application of a [topological sort](https://en.wikipedia.org/wiki/Topological_sorting) algorithm gives an ordering of what to do, and summation of the topological sort with concurrent elements counted once only gives an overall estimate of the schedule length.

Like all other data structures for allocation, Gantt charts have a built-in opinion on what the resources in question _are_, and how they should be allocated for best usage. Therefore they are _political_ tools, with an opinion on what kinds of computation and which kinds of resources are being allocated. They encode a structural point of view on power which is suited to industrial work: A task, to a Gantt chart, is well-defined, and doesn't require any political power to carry out, and isn't entangled in a web of credit allocation - the web is a task for other people, the independent contributor just executes.

This is not great news if you have a Gantt chart _dictated to you_. But there's no affordance within a Gantt chart for Gantt charts to _not_ be dictated to you, no handle to twiddle as an independent contributor. So the negotiations inherent in this relation are off to a bad start, for the seasoned hand, and does not even occurr to the beginner.

(If you need another example on how data structures can be _political_ in nature, consider the British person's perennial complaint about how foreigners _cannot queue worth a damn_ - because the foreigners don't respect the point of view on resource allocation that queues represent, where there is a resource or pool of resources which are to be meted out to people in first-in first-out order).

That political worldview inherent in the Gantt chart has a crisp source. It's from H. Gantt creating them them to organize industrial plants, in imitation of the organization of his father V. Gantt's slave plantation, although to his credit H. Gantt at least disavowed slavery as a way to organize society. (C. Rosenthal has [a monograph](https://www.amazon.com/Accounting-Slavery-Management-Caitlin-Rosenthal/dp/0674972090) on this.)

The world is different today, one hopes for the better. In modern independent work, there must be political observance given to the voice of participants in the work - modern work is differentiated in that the individual worker also participates in deciding what work to do and how to do it.

This is particularly pronounced in highly technical work because management often doesn't have observability in how to do highly technical work at all. Workers in that case often must be the whole arbiters of, or huge contributors on, how to do the work. They also get great influence in what work to do. This is an etic factor, a factor from the external point of view, in the Gantt chart, because of its roots in tasking of individuals without individual power.

## Mertonon

The doom that comes upon the independent contributor is political in nature, but it is also _data-structure-driven_ in nature, inasmuch as data structures encode political points of view on resource allocation. Therefore an attack on the problem might be giving people a new data structure to more directly do politics with.

I have implemented such a thing and given it a server, UI and UX, and packaged it into a jar. It's called Mertonon.

Mertonon is a new way to plan and budget for orgs, for any kind of budget. With Mertonon, you make a picture of your org as a neural network, as a data structure. You do this by going on Mertonon and linking together local, political, human judgements of impact with respect to KPI's. These don't pretend to be objective or apolitical as many Gantt chart estimates do. Also unlike Gantt charts, Mertonon itself will suggest changes to your budget based upon those judgements.

######
###### salesmanship for mertonon
######
######

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

Often, Gantt charts are for external stakeholders of some kind, as other budgets are. But they're not actually statements about putative base reality, so sharing partial Mertonon political pictures should be as mollifying.

### what happens for when things are bad faith

We also fully intend that Mertonon's suggestions also work for the very specific quotidian purpose of looking good and blaming someone else for your failures. A guide for that and countermeasures and counter-countermeasures and counter-counter-countermeasures is forthcoming whenever Mertonon allocates effort to it.
