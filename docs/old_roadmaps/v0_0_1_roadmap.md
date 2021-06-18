## Roadmap before pre-alpha release

No more target for release lol

Think on some specific fit and finish tasks as you get to them because you want high locality for these

- [x] Forward and backprop on layers and weights
- [x] Generated data, incl. dag representation
- [x] Web API for generated data
- [x] BE testing first pass

- [x] Individual grid view
- [x] Individual weightset view
- [x] Individual layer view (with adjacent weightsets)
- [x] Individual cost object view that people live in (with adjacent cost objects)

- [x] Backend DB CRUD for postgres (20% for CD, 80% for RU)
- [x] Web API for DB CRUD (20% for CD, 80% for RU). No FE work
- [x] BE testing second pass (add DB testing of some kind)

- [x] Accounting learnin'

- [x] PoC Data augmentation (in separate python bit)
- [x] Go ham on model tests
- [x] Undo shimming of weights

- [x] Nav
- [x] Sidebar with internal views of some kind
- [x] Grid selection view
- [x] Better model views
- [x] View simulacrum of full results

- [x] Model create/delete: grid
- [x] Entry view
- [x] Non-generated view backends
- [x] Non-generated view frontends
- [x] Model create: rect / layer
- [x] Model create: line / weightset

- [x] Config generates

- [x] FE tests. Have exactly one...
- [x] Model create cobj
- [x] Mutations in the sidebar nav bug (refactor to do links that mutate both sidebar and main screen)
- [x] Flow partially-displaying-things bug

- [x] Entry values
- [x] Get rid of denoms, call them values. Renormalize a la minute.
- [x] Actual loss calculations
- [x] Derive normalizing op
- [x] Start semi-warm reachouts
- [x] Implement normalizing op

- [x] Add in the actual application of normalizing op
- [x] Decent default for nil weight normalizations
- [x] Grad column for weights, delta column for cobjs, pass through nullable bits all the way to fe
- [x] Upserts BE
- [x] Serde in grad service display for grads for weights and deltas for cobjs

- [x] CLJS: janitor for shadow-cljs. get it to display anything.
- [x] CLJS: setup reitit routes
- [x] CLJS: nav pages

- [x] Sidebar setup again
- [x] Action routers
- [x] Grid select
- [x] Testing yakshave for cljs...
- [x] Properties for grid select, just exercising... - fold in all the other properties into same task
- [x] Flow again, mark 3
- [x] Flow losses display / loss view
- [x] Add activation semantics into cobj schema
- [x] Add the activations when we're shoving things into the cobj
- [x] Make a net gen that will have that augmented forward pass and grad semantics
- [x] Incorporate forward and grad semantics into the gen net api
- [x] Layer display + sidebar. With activations, and histogram view
- [x] Weightset display + sidebar
- [x] Cobj / entries display + sidebar

- [x] Actually schlep the weight grads to generated endpoints
- [x] Indiv weight display + sidebar
- [x] Check if the cobj deltas are actually network values or activations - figure out the activation adjustments actually needed if we're applying net activations to activations...
- [x] Power law entry distribution
- [x] Matrix histogram color view for weightset
- [x] Matrix histogram modal view for weightset adjustments

- [x] Enforce everything-activation, everything-has-delta, everything-has-gradient property test
- [x] Fix the big-ass delta vals problem for the input cobjs

- [x] Fix the missing cobj problem

- [x] Input layer indications (like the loss bits)
- [x] Landing page copy (just copy readme)
- [x] Munge Landwind
- [x] Mailing list setup
- [x] Mailing list embed onto website

- [x] Whack the serde fn names properly by reifying fact that they're type mashing fns
- [x] I want clear and smooth json field semantics, not the jank.

- [x] Get all the inputs and all the losses (and the individual loss) in the grad fn

- [x] Get all the db tables to use mertonon postgres namespace schema
- [x] Generator hyperparams (parametrization) for test vs. demo flow

- [x] Table versioning. With info in the generates for same. No actual versioning logic as of yet

- [x] Get the overall JSON parser thing up in IO utils with protocol extensions and get rid of cheshire entirely
- [x] Created times. Not generated, just current time.
- [x] Updated times, including actual semantic significance. Not generated, just current time.

- [x] Optimizer type and optimizer hyperparams in grid.

- [x] Name examples
- [x] Generation of names for all net vals
- [x] Generation of labels for all net vals
- [x] Unbreaking the tests after generation

- [x] Add in the prompt, hardcoded for now

- [x] Grad manual kickoff API endpoints with dates, using grid optimizer hyperparam. No autocalc lol. No button in view as of yet.
- [x] Follow through names to view

- [x] Grid create
- [x] Grid view with real data
- [x] Allocation cue - API endpoint in API.clj

- [x] Layer create action route, hits API endpoint
- [x] All the other action routes at once lol: weightset, weight, cobj, entry, inputs, losses
- [x] Layer create sidebar route
- [x] All the other sidebar routes: weightset, weight, cobj, entry, inputs, losses

- [x] Grad kickoff page: start it
- [x] Layer create link in grid sidebar
- [x] Layer create sidebar modal creation
- [x] Add isDemo modal properly and trace it along things... maybe a global?
- [x] Figure out how to clean up state when we unmount react components...
- [x] Layer view with real data, rig in the existing thing

- [x] All the other sidebar create links in every sidebar... (weightset, weight, cobj, entry, input, output)
- [x] Grad kickoff page: link it from somewhere
- [x] Shove in scrypt

- [x] State machine for spinnies and so on. First grid thing only
- [x] Link up state machine with transitions (that just print)
- [x] Have actual viewable portion for spinnies and so on, grid only
- [x] Apply state machine to delete and link up
- [x] Do the creation bit

- [x] Nicer finalization
- [x] Reverse path mapper working
- [x] All the other views with real data, rigging in existing stuff, switch on demos
- [x] Generalize viewable portion thing to all the forms

- [x] Get all the other sidebar modals (grid, layer, ws, cobj, input, loss)
- [x] Weight, entry sidebar modals

- [x] Put POST health checks in not only for health check but for warming up... start off with one in order to whack slow-ass creates, recognize that this is gonna be a weird acling eventually
- [x] Rig up non-crap finishing events
- [x] Fix history not working for main view - it should just work for main view

- [x] Multiple inputs need state setting
- [x] Range input for weight, think about scaling later...
- [x] onPaneClick click

- [x] Weight state thing - single vs double click link, what a thought
- [x] Make sure we can delete everything

- [x] Really actually deal with the setup state vs. per-render state in every endpoint - not doing type 2 was mistake
- [x] Follow through labels to view

- [x] Grad kickoff page: date modals
- [x] Grad kickoff page: button
- [x] Allocation cue - Shove it in the weight creation modal
- [x] Entry maker in cobj view
- [x] Cobj maker in layer view
- [x] Weight maker in weightset view
- [x] Template in url host into index.html, then get api.js to deal with it

- [x] We seem to need validations for any serious usage at all... add in validations and validation setters to the state machines. FE only.
- [x] Configs in util configs - url host, prod vs dev mode. Hierarchy of configs

- [x] Coerce names not blank, fkeys not blank, value number (fe only)
- [x] Make weight and weightset checks into validations

- [x] Url format thingies
- [x] Links to overall grad in everything, and bigger link to overall layer in cobj, weightsets in weight

- [x] I don't think we need more work besides the fillers
- [x] One input and one loss per grid only for now(fe only)

- [x] Acyclicity testing (fe only)
- [x] Toast situation is ridic... need poppers somehow
- [x] Actually replace all the validations with poppers

- [x] Coerce inputs and losses not being the same damn layer (fe)
- [x] Coerce non duplicate weights (fe)
- [x] Coerce non duplicate weightsets (fe)

- [x] Vendor the css bits
- [x] Decision: whack things in the denormalization
- [x] Kill journal entry making from cobj if its not loss or inputs (fe)

- [x] Grad kickoff validations in specific, because they'll basically be a long checklist... maybe yet another validation view thing for that?
- [x] Grad kickoff page: happy case, get it to work

- [x] Grad kickoff page: happy case, get it to actually do what it says it will do

- [x] Get the 1d vs 2d norm thing down properly in python
- [x] Fix the weight 1d vs 2d norm being weird bug by making a second norm for 2d
- [x] Infinite weight yakshave and whacking other parts of the long bug

- [x] Scaling dout to get gradient flowing by fiat. Also scale the weight adjustments - separately!

- [x] Make entry not integer valued and shove 0 + epsilon currency semantics into em
- [x] Do not do the input output profit gradient thing, it doesn't seem to work - plain subtrahend works...
- [x] DB schema-tide. Enough for username/pwd with room for OAuth, OpenID, SAML, FIDO stuff, even if we don't implement yet

- [x] Think a little on annotation-lang

- [x] Plain inner join
- [x] Generative test inner join somehow... maybe just exercise
- [x] Icon-tide. Weirdo free icons for kicks and for nonliterate recognizability

- [x] Multiple join and test
- [x] Re-codd-normalized after query (instead of existing simple row-member thing - this way we can just use it after normalization...)
- [x] Make grid actual proper parameter in grad-create!, filtering out entries not from grid, lol. Then test
- [x] Mega-join thing for grid view for validation for kickoff, with consonance and consonance test

- [x] Per-grid grad and weight apologies ceteris paribus, punt on last mutation dates

- [x] Poking around manually-like
- [x] Put `mount` in and be done with the hinky server reset shenanigans
- [x] The stupid dynamical attractor thing
- [x] Competitiveness margin docs and frontend - make it actually tell what its doing
- [x] Jar builder code https://kozieiev.com/blog/packaging-clojure-into-jar-uberjar-with-tools-build/
- [x] Whack scary abs fail load messages
- [x] Whack scary cljs fail load messages
- [x] Yakshave jar upload to clojars and pomfile

- [x] Start homepage
- [x] Link to gh install from homepage lol
- [x] Get contact form from mailchimp

- [x] Switch out repl handler, see if defstate will just handle for me

- [x] Fkey simple join validation
- [x] Stupid input output joins validation scoped to grad only.

- [x] Rename 'p&l' to competitiveness everywhere

- [x] Fix the simple join validation not working
- [x] Negative things... have meaning i guess, put in some ui thing for them
- [x] Broken back-to-grid link in demo
- [x] Still general clicking around
- [x] No nullable columns. No more. Make them all non-null and just blank now
- [x] Feature flags for real - file only

- [x] Setup readme detailled outline
- [x] Narrative usage doc detailled outline

- [x] S3 hosting, yakshave it - avoid iac for now, tbh
- [x] Get website up. Only mailing list and install linked up
- [x] Yakshave email on domain - also avoid iac

- [x] Setup readme write
- [x] Narrative usage doc fill-in

- [x] Emergent doesn't matmul bug
- [x] Usage gifs: tutorial steps
- [x] Emergent matmul reverse op bug
- [x] Emergent cobj deletion not cascading bug
- [x] Build, click around and upload first release - public repo, protect master, megasquash
