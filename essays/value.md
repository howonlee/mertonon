# Towards a Pure-Computational Theory of Value - Mark I, pre-alpha

Outline

- There exist a lot of facts.
- There also exist a lot of variables in the world.
- Economics is about dealing with the constraints the facts have on the variables.
- This is an instance of constraint satisfaction.
- Therefore, the main meat of economics is computational in nature.

- Unlike most CSP instances, economic systems are required to be anytime, highly available, algorithmically online, legible to even the illiterate, distributed and durable even towards war, systematic attack and disaster.
- This is why the signature data structure of the economic CSP is the set of prices.
- A theory of value is a theory of why prices exist and how they are determined. Our contention is that prices are a data structure with respect to the variables in the world and the constraints upon those variables.
- We do not respect the _coloring_ of variables like other previous theories of value - this is one formal way to put Sutton's bitter lesson, which is entirely empirical in nature.

- Tantonnement, thus construed, are an algorithm on that data structure. The algorithm is just gradient descent
- However, the attention of economics has not been on the internal representations with respect to the outer data structure that the available prices represent.
- This is not the focus of neural network people. Neural network people are all about the internal representations.
- In order to deal with the internal representations, you need the chain rule and caching (reverse autodiff).

- Price representations have to have the property of scale-freedom because peeps do futz with the scale non-marginally
- Like other instances of gradient descent, subject to the renormalization group
- Inequality from power law non-critical renormalization group. Instead of the scale-freedom coming from formal properties of criticality, it comes from formal upfront properties of prices
- Allocation is known to be NP-complete. These all have ordinary critical renormalization group semantics in discrete decision formulation

- Class because NP-complete problems are amenable to caching, in addition to the already mentioned chain rule caching structures
- Compare to the misnomered attentional structures or sigma-pi structures
