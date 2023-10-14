Many of these answers are obviously not favorable for serious use at this time. However, they can be amended, sometimes quickly, given any interest you may have, and we will improve every answer as time and development goes on. We took this from a typical SaaS DDQ. If you wish to ask any more questions of any kind, contact Howon.

## What drives the cost of the solution? Users? Transactions? Bandwidth or storage consumed?

TBD. We will have an answer for you when we start charging. We don't want to disincentivize cost-node-decides-their-own-weight flows so we are going to have a generous price-per-seat if we charge for seats. There will be a separate fixed-price-per-time-period low-touch pro-but-not-enterprise SaaS or on-prem service in addition to an enterprise offering.

## How often can the pricing change and by how much?

For enterprise customers, pricing will only change whenever contract renegotiations occur on renewals. For low-touch professional customers, pricing can change at any time but with 30 day's notice.

## What is the length of the standard contract? What terms exist around cancellation? Are there severance charges or any long-term costs?

We plan to do month-to-month length of contract for the low-touch professional level. Cancellation will be at will for the professional level. There will be a money-back policy for the low-touch professional level. There will be a free trial for the low-touch professional level. There will not be a severance charge.

Everything contractually enforcible will be negotiable for the enterprise redlined contracts. If you want to only be given support by people with rubber pancake hats, we will sign such a thing, for adequate consideration if we are able to come to an agreement.

## How do you protect your data? Is encryption used? Is industry standard encryption used (AES 128 or AES 256)? Is it proprietary encryption?

Currently there exists no authorization solution and only a minimal authentication solution. We're going to change that real soon but we plan to do all significant authentication methods, explictly including SSO with significant providers, LDAP, AD, Oauth, lots of other stuff and a RBAC-based authorization solution.

The data is in a Postgres DB controlled by customer, on premise, at this time. Therefore, it's customer's right and responsibility to choose how to encrypt their data at rest. See the [official Postgres docs](https://www.postgresql.org/docs/current/encryption-options.html) for options. Mertonon is not affiliated with the Postgres Foundation in any way, shape or form. We will implement SSL connection to the DB when we do it.

## How and where is your data stored? What is the data backup and off-site storage schedule?

Data is stored in customer databases, at this time. Data backup and off-site storage responsibility is currently upon customer. We will provide a cloud hosted service when we get around to it.

## Do you operate your own data center? What level is the center? How secure is the facility?

We do not operate a data center at this time. We will eventually, whereupon we will actually answer this question.

## How do you ensure that only authorized personnel have access to your data?

Currently there exists no authorization solution and only a minimal authentication solution. We're going to change that real soon but we plan to do all significant authentication methods, explictly including SSO with significant providers, LDAP, AD, Oauth, lots of other stuff and a RBAC-based authorization solution.

## Do you have a business continuity plan? How are you supported in case of an outage?

TBD.

## What are your support hours? How quickly will problems and issues be resolved?

Support hours are currently regular Pacific time business hours (9am-5pm). We will have a 1 business day turnaround for e-mailed inquiries for low-touch paid plans when we start charging. For regular issues for community edition and low-touch SaaS we plan to do a [software martingale](https://howonlee.github.io/2022/06/27/Martingale-20Project-20Timing.html) with O(n^2) growth, so checkins at triangular numbers of business days (1, 3, 6, 10, etc), not exponential numbers of business days.

Enforced project and issue deadlines as well as expanded support hours are available with enterprise contracts.

## When and how often is routine maintenance scheduled?

For the self-hosted option, you can schedule routine maintenance and updates whenever you'd like, although we plan for it to not require outages for maintenance except in extremis. When we do the cloud hosting option, we plan to not require outages for maintenance except in extremis.

## If I cancel my subscription, what happens to my data?

For the self-hosted option, your license will revert back into the free community tier and you can continue using that as you wish forever, including export of data.

For the cloud hosted option, when we have the cloud hosted option, we will give you 12 months to export your data.

## Is there a Service Level Agreement (SLA)? Do you guarantee 99.9% uptime and spell out financial penalties should the promised level of service not be delivered?

We will guarantee 3 9's of SLA, with financial penalties, for cloud hosted enterprise contracts only, when we do them.

## What are the privacy policies in place? How are customers notified if policies change?

TBD

## What jurisdiction are you under?

We are a California organization. We have not incorporated as of this time. We will incorporate before taking any money for anything. When we incorporate we will do it as a Delaware C Corp, as usual.

## Who is liable in a breach? How will you respond? Are you obligated to notify me? If so, in what time frame?

TBD

## Does the system meet my compliance and regulatory requirements? How will disposal of sensitive data be handled?

TBD

## Do you have strong financial backing?

We have a small amount of bootstrapped financial backing (30 months runway as of writing), some amount of which is in money markets and some amount of which is still in our ownership of mutual funds. We will not delay profitability for putative growth.

We will probably make this question available at request as opposed to available openly at some point in time.

## How many employees do you have overall? How many provide support?

It's only Howon, for now. And Howon's going to be doing your support, too. There exist some people who still contribute to open core projects, despite this not being completely open source - if an open source community coalesces around the open source portions of Mertonon, that could be a material factor in support, otherwise not.

## Do you have adequate employee screening and background check procedures?

When we get some employees we will use a SaaS solution for this.

## Do you use sub-vendors? If so, what is the full chain of activities and responsibilities in the event of an issue with a sub-vendor?

As is usual for software, we use many software libraries which are licensed variously to us via open source licenses. These licenses are licensed to us invariably with limitations of liability and disclaimers of warranty, as we ourselves limit our liability and disclaim our warranty for the community section of our software. Therefore, when there is an issue with these, we will either solve the issue on our side, change the library out, or ask the OSS maintainers if they would be willing to work on the issue, depending upon severity and urgency of issue and our understanding of the libraries.

We do not use SaaS sub-vendors for delivering the service to you at this time, with the exception of git hosting. This will change when we actually do cloud hosting. At that time we will determine the processes of issues with sub-vendors.

## What happens if you go out of business?

In the case of going out of business, we will rip out the license-granting portion of the project and go full OSS while we go and get jobs or something. We will make a best-effort attempt to give support for 6 months for essential issues and we will steward the project as an ordinary OSS project after that.

If we have had developed the cloud hosting by that time, we will give you 12 months to export your data and put it into self-hosted instances.

Be advised that the situation of going out of business will almost surely entail materially worse development and support speed and quality.
