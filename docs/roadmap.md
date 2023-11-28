# On Deck Roadmap (some small subset of these coming in the next big version, while we crank out small versions)

Community edition unless noted otherwise or we have a fit of pique or something. The vast majority will be CE, is what I'm saying, basically.

Also look at small tasks roadmap

- Sponsor newsletters
- Prezzo
- Auditing BE w/ undo stuff
- Auditing FE
- Implement norm layer op in normal net and write about how the norm layer op is cool and good
- Some other essay
- Try again on FE tests
- Mass CRUD ops FE, incl. weight painting
- Staring at credit allocation semantics both in docs and program. True conservations, along with property - then liberate the input and loss cardinalities
- Take-the-suggested-weight-change updates
- Kick off forward passes every time we change anything, async (I think as FE event to BE trivial service thingy)
- Munge and validate password according to a serious password-based-auth checklist, BE then repro FE
- Cut alpha, sponsor more newsletters

- Easy SSO's BE
- Easy SSO's FE
- Whack a profit thing out for real, refactor the competitiveness thing to budget utilization
- EE plugin making - dynamic loading? not doing dynamic loading? doing funky stuff? some registry or something?
- Make some part of auditing EE properly
- RBAC BE. EE plugin for full RBAC, CE for general user-admin thing
- RBAC FE
- Full grad view (exec summary view) FE
- Public instance
- Cut pre-8x-beta, more sponsorships

- Integration DB thinking
- Email integration BE
- Email integration FE
- Crontab for adjustments, exec summary view, emails
- Bigger and markdownified label input - maybe with preview. Leave room for "form-language"
- Sorting / Pagination BE
- Sorting / Pagination FE
- Searching, sorting and paginating on matrices - other sorts of things dont have to care but we do have to care. The two-search method
- Cut pre-7x-beta, more sponsorships

- I would like first real release to have autoupdater

# General Future Roadmap (not in any real order)

DSL's are terrifying epic quests in non-homoiconic languages but not too bad when you do have homoiconicity, so don't be scared when we just list "oh, DSL for this, DSL for that" through these places. I think they're a viable way to do the unfortunately inevitable inner platform without our skulls softly bleaching in the desert of the inner platform effect because they'll be pre-serialized, basically - they'll be small JSON lisps, and we'll expect peeps to write them by emitting them from ordinary programs in some other language, so you'll never have to touch Clojure or even Java if you don't want to, although frankly both are ridiculously better than ABAP. They'll be locked down pretty tight permissions-wise, though, so the logic programming thing comes first, and the vermintide implicit in this approach awaits us. We claim that property testing is a good weapon against this tide of vermin, but that'll sound better if (when) we actually whack enough of the bugs.

- Sidebar history forwards
- RG for computer scientists (towards RG for salespeeps)
- License design and server
- Mertonon voting method (somehow)
- Bookmarks BE
- Bookmarks FE
- Get SQLite compatibility and bundle with SQLite so you just download and play.
- Plugin system (both for CE and EE stuff). No third-party usage or docs intended yet
- Low-touch paid option (Stripe integration on homepage - integration for paying to use Mertonon, not integrated into Mertonon, keygen impl, integrated licensing)
- Full loss views, full input views
- Password reset
- Cobj and weight histories, maybe intertwined with the general auditing, maybe not. With generates and all...
- Data in validation failures which are currently keyword-based so you could do bounce-off-the-guardrails usage
- Other KPI's
- Url endpoint refactor to make demo / nondemo basically actually transparentish
- Admit the tyranny of the notes thing and do a notes / documents / form state machine DSL that's like, queryable
- Currencies
- Explicit and compact FE modes. Property: they always gotta have the same links, just different display
- Ordinary operation DSL
- Deal with giganto-spam property tests
- Undo / redo + soft deletes (use archive tables)
- Sidebar histories
- EE build separation
- EE: Whitelabelling
- Minimap-like thing
- Get the video done
- Theory of value writeup - mark 1
- Theory of inequality writeup - mark 1. I prolly need an invariance principle (like w/ the Gaussian measures on boolfuncs) for levy-stable stuff instead of just Gaussian to whack a Friedgut's thm out of it, to frolick around in neural net land, really
- Theory of class writeup
- Search
- Clj-kondo in CI/CD
- Third party plugin use docs and examples
- Stateful action testing, lol

- Integrations with CRMs, ERPs, HR management, billing and procurement platforms. I have a gigantic list. Some will be EE.
- Full double-entry financial accounting section above and beyond the managerial responsibility accounting. Also differentiable, but also figure out how to reconcile the differentiability with ASC 606, GAAP and SOX compliance, among others. I don't think it can be considered absorption costing, so GAAP may be off the table entirely.
- Non-GAAP stuff that people want. EBITDA, etc etc. Probably a DSL for them and instantiate
- Gigerenzist uncertainty module I guess... wtf that entails
- Self-contained binary
- Autoupdater

- SAML auth
- EE: LDAP auth
- JWT auth. complain a lot upfront when doing it about not being compliant with anything, which it won't be if you use JWT auth
- Weird and non-weird other SSO and authn, authz. Some of it may be EE
- Get MT to run on absolute shitboxes. Like, lowest-marque raspberry pi's, the worst EC2 instances, etc.

- Document uploads and integration with cloud storage things
- Managed cloud hosting
- 2-factor authentication
- SOC2 certification. There's a menagerie of things to do with this.
- All these certs definitely assume in important ways that there's more than one person in your firm, and frankly enterprise sales is hard going without at least a SQLite amount of people. So figure out something to get more people
- ISO27001 cert. Also a menagerie of things to do with this

- Notification page
- Revenue recognition DSL. An entire DSL just for programmatic revenue recognition. Also purchase/vendor management
- Variance measures and notifications
- Materiality measures and notification
- BOM and production management, rudimentary
- Rudimentary CRM
- Inventorying, rudimentary
- Warehousing, transportation

- Eutrophication detection
- Self-licking ice-cream-cone detection
- Little-data bootstrap (founder) module of some kind
- Local maxima detection
- Temperature, annealing system, periodic adjustment rate adjustment

- Import and export from common data formats in addition to Excel (CSV, JSON) as well as an open Mertonon file format. I'm going to be real with you, it's probably going to be a fancy file extension for a zip of csv's or something
- Abuse activitypub terribly
- Different neural topologies, including recurrent, recursive, attentional. Probably attentional first
- Different gradient-finding algorithms, optimizers, higher-order algorithms, pending on them working well.
- Mechanisms for ameliorating inequality and underlying induced putative hierarchy in assignment. Or exacerbating it, you do you
- Rapid random reset and varied adjustment rate options. There's known lack of consonance between NP-hard stuff and the phase transition stuff (contingent on NP-complete decision problems, as opposed to optimization problems) which is kind of an open problem right now, unfortunately
- XORSAT-like domains (linearly soluble domains)

- Local-global soft hashes

  - A personal hash that encodes somebody's relations of production with respect to the global an organization
  - The underlying idea is a pretty standard random-projection LSH, only not random, not an ordinary projection and with an idiosyncratic hyperplane set
  - Would like hierarchical semantics, so that the first bit or whatever is least specific, last one is most etc etc
  - With a coherent publicizable API so third parties can verify. Or don't publicize it and do the HR employment verification thing
  - With a recommendation program to slot them into a new position in a different org
  - Personal hash may be pretty opaque

- System for wrangling transfer pricing into this more directly somehow
- Something for being all recondite and euphemistic about credit allocations in the American or Japanese style
- Network community detection dealios so you can cleave off "natural" spinoffs
- Mechanism for mitosis and merging of instances

- Repurpose the hot-paths paper for our purposes
- Simulation and counterfactual tooling
- Advanced anti-gaming-the-system countermeasures
- Anti-bullwhip stuff
- Countermeasures for more sophisticated algorithmic attacks, such as adversarial weight choices
- Diagnosis of non-adversarial but still degraded patterns of assignment and recommendations for remediation.

- Internationalization and localization and accessibility work
- Lock down browser support
- Phone apps
- Translation to currency values instead of just renormalized fraction stuff. Probably this entails integrations to third parties who do just this, and display exchange rates - and use exchange rates in multi-currency situation

- Tax module. Start with America, special focus on the Delaware C Corp and their foibles. I recognize that this is a thing of terrible, infinite scope.
- Compliance with the Euro AI act, whenever they actually name and pass it
- GDPR/CCPA-specific compliance tooling.
- NIST-800-53 compliance
- FedRamp
- CSA STAR

- Pentest reports for enterprise customers
- Enterprise change management
- Enterprise white-labelling

- Some features for unions and cooperatives, although I haven't decided on those yet. Feel free to contact me if you're a big or medium-sized muckety-muck at one of those.
- External capital allocation (hedge fund / portfolio / VC) module, with the incredible amount of compliance that this entails. Perhaps an entirely separate company one of these days.

It is the inevitable complaint of people upon encountering an open core project that it's going to turn into some bait-and-switch thing where hard upsells are going to be a thing. This is not going to happen, even if I have to browbeat people into it. We're gonna keep up an 80/20 ratio of OSS stuff / EE stuff in the first-party development.
