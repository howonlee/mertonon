# Towards a Pure-Computational Theory of Value - Mark I, pre-alpha

Outline

- There are a lot of facts about the world.
- There also exist a lot of variables in the world that people can change.
- Economics is about dealing with the constraints the facts impose on the variables.
- This is an instance of constraint satisfaction.
- Therefore, the main meat of economics is computational in nature, or at least under the grand regime of constraint satisfaction, which is the underlying theoretical basis of most AI that does things successfully.
- I am therefore less interested in looking at AI from an economic point of view than in looking at economics from an AI point of view.

- Unlike most CSP instances, economic systems are required to be anytime, highly available, algorithmically online, legible to even the illiterate, distributed and durable even towards war, systematic attack and disaster. They also have to be durable to poor problem-posing - they have to elicit their own problem statements - and incomplete knowledge.
- This is why the signature data structure of the economic CSP is the _set of prices_.
- Our contention is that prices are a data structure with respect to the variables in the world and the constraints upon those variables.

- A theory of value is a theory of why prices exist and how they are determined.
- We do not respect the _coloring_ of variables like other previous theories of value - this is one formal way to put Sutton's bitter lesson, which is entirely empirical in nature.
- Examples of _coloring_ of variables is the labor theory of value, the cost-of-production theory of value, utility theory of value, intrinsic theories, and that sort of thing.

- Consider Tantonnement.
- Tantonnement is L. Walras's way that prices are determined, a _groping_ of local derivatives on a price surface at auction.
- Tantonnement, thus construed, are an algorithm on that data structure of prices. The algorithm is just gradient descent. The neural network peeps approximation is SGD but the tantonnement formulation is continuous.

- People reject tantonnement by the nonconvexity theorems. But nothing in life is actually convex, even approximately. Local optima are omnipresent in all economic systems.
- We have a lot of working nonconvex optimization in the last 10 years from massive computation, from the neural net people.
- The attention of economics has not been on the internal representations with respect to the outer data structure that the available prices represent.
- This is not the focus of neural network people. Neural network theory people are all about the internal representations.
- In order to deal with the internal representations, you need the chain rule and caching (reverse autodiff).
- The iceberg of the price is in the internal representation of the vast chain of other prices that it takes to get to that individual price - from the commodities to the widget to the labor, these are all internal representations, not external ones.

- Renormalization group analysis of inequality and class is coming.
