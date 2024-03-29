(ns mtfe.views.intro
  "Introduction view to look at while you fill out deets in the intro sidebar")

(defn intro-page [_]
  [:div
   [:h1 "🥞 Mertonon"]
   [:h2 "Open Post-Pre-Alpha"]
   [:p "Mertonon is a tool for neural organizational management."]
   [:p "In accounting terms, Mertonon is a tool for semi-computational attribution of
       P&L to individual cost objects within responsibility
       centers, for an overall P&L. More than one P&L, targets besides conformance, and other KPI's forthcoming."]
   [:p "In computing terms, Mertonon is a neural network model of your organization."]
   [:p "Create an administrator account from the sidebar."]])
