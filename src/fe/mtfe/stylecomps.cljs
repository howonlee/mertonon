(ns mtfe.stylecomps
  "Style components for Mertonon frontend.

  Get all the long sequences of tachyon classes :div.blah.whatever.foobar.thingy done here
  so we're not cluttering up the rest of the codebase. Also for consistency, of course

  If there's any state at all to the component, it probably belongs somewhere else")

;; ---------
;; Containers
;; ---------

(def whole-page :div.f6.avenir.white.bg-dark-gray.w-100.h-100.overflow-auto)

(def popper :div.avenir.white.bg-dark-gray.pa3.ma3.ba.br2.b--silver)

(def main-section-container :div.pa3.fl.w-60.h-90.overflow-auto)
(def main-section :div.fl.w-60)

(def main-sidebar-container :div.fl.w-40.h-100.bg-mid-gray.pa3.overflow-y-auto)

(def nav :div.flex.items-left.justify-left.h-5.w-100.bg-black.white)

;; ---------
;; Icons
;; ---------

(def plus-icon :i.fa-solid.fa-plus)
(def trash-icon :i.fa-solid.fa-trash)
(def pen-icon :i.fa-solid.fa-pen)
(def spinny-icon :i.fa-solid.fa-circle-notch.fa-spin)

;; placeholder that takes space
(def blank-icon :i.fa-solid.fa-plus.o-0)

;; visual language
;; besides the on-the-nose stuff, just do rando fun animals
(def grid-icon :i.fa-solid.fa-border-all) ;; grid
(def layer-icon :i.fa-solid.fa-hippo) ;; layer => hippo
(def cobj-icon :i.fa-solid.fa-kiwi-bird) ;; cobj => kiwi bird
(def ws-icon :i.fa-solid.fa-crow) ;; weightset => crow
(def weight-icon :i.fa-solid.fa-cat) ;; weight => cat
(def entry-icon :i.fa-solid.fa-shrimp) ;; entry => shrimp

;; ---------
;; View Components
;; ---------

(def flexwrap-container :div.mr3.flex.flex-wrap.justify-center)
(def rounded-button :div.br2.pa3.link.dim.pa3-ns.mr3.mv3.ba.white)
(def grid-button-container :div.relative.br2.pa3.pa3-ns.mr3.mv3.ba.white)
(def grid-button-trash-container :div.absolute.top-0.right-0.f6.link.dim.ph2.pv2.mb2.dib.white.pointer)

(def main-table :table.f6.mw8.center)

(def border-region :div.pa3.ba.br2.b--silver)
(def mgn-border-region :div.pa3.ma2.ba.br2.b--silver)

(def table-head :th.fw6.bb.tl.pb3.pr3.w5)
(def table-member :td.pv3.pr3.bb.b--white-20)
(def hist-container :div.w-20.h-10.ba.b--white-20)
(def histogram-head :th.fw6.bb.tl.pb3.pr3.w-20)
(def histogram-member :td.bb.b--white-20.h3.w-20)

(def weightset-table :table.ba.br2.b--white-10.pv2.ph3.mt4)

;; ---------
;; Small Misc Components
;; ---------

(def button :div.link.dim.ph3.pv2.mb2.dib.white.bg-black.pointer)
(def disabled-button :div.link.ph3.pv2.mb2.dib.silver.bg-gray)
(def link :div.link.dim.pointer)

(def form-label :label.pa2.mb2)
(def input :input.input-reset.ba.b--black-20.pa2.mb2.db)
(def checkbox :input.ma2.w1.h1)
(def select :select.ba.b--black-20.pa2.mb2.db.w-100)
(def select-option :option.b--black-20)

(def validation-toast :span.light-blue)
(def scary-font :span.red)
