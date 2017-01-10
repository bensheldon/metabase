(ns metabase.api.public
  "Metabase API endpoints for viewing publically-accessible Cards and Dashboards."
  (:require [cheshire.core :as json]
            [compojure.core :refer [GET POST]]
            [schema.core :as s]
            (metabase.api [card :as card-api]
                          [common :as api]
                          [dataset :as dataset-api])
            [metabase.db :as db]
            (metabase.models [dashboard :refer [Dashboard]]
                             [dashboard-card :refer [DashboardCard]]
                             [hydrate :refer [hydrate]]
                             [public-card :refer [PublicCard]]
                             [public-dashboard :refer [PublicDashboard]])
            [metabase.public-settings :as public-settings]
            [metabase.query-processor :as qp]
            [metabase.util.schema :as su]))

;;; ------------------------------------------------------------ Public Cards ------------------------------------------------------------

(defn- run-query-for-card-returning-results-in-format [card-id parameters & [results-format]]
  (let [parameters (json/parse-string parameters keyword)]
    (binding [api/*current-user-permissions-set*     (atom #{"/"})
              qp/*allow-queries-with-no-executor-id* true]
      (case results-format
        nil    (card-api/run-query-for-card card-id, :parameters parameters)
        "csv"  (dataset-api/as-csv  (card-api/run-query-for-card card-id, :parameters parameters, :constraints nil))
        "json" (dataset-api/as-json (card-api/run-query-for-card card-id, :parameters parameters, :constraints nil))))))

(api/defendpoint POST "/card/:uuid"
  "Fetch a publically-accessible Card. Does not require auth credentials. Public sharing must be enabled."
  [uuid format parameters]
  {format     (s/maybe (s/enum "json" "csv"))
   parameters (s/maybe su/JSONString)}
  (api/check-public-sharing-enabled)
  (api/let-404 [card-id (db/select-one-field :card_id PublicCard :uuid uuid)]
    (run-query-for-card-returning-results-in-format card-id parameters format)))


;;; ------------------------------------------------------------ Public Dashboards ------------------------------------------------------------

(defn- uuid->dashboard-id [uuid]
  (api/check-404 (db/select-one-field :dashboard_id PublicDashboard :uuid uuid)))

(api/defendpoint GET "/dashboard/:uuid"
  "Fetch a publically-accessible Dashboard. Does not require auth credentials. Public sharing must be enabled."
  [uuid]
  (api/check-public-sharing-enabled)
  (hydrate (Dashboard (uuid->dashboard-id uuid)) :ordered_cards))

(api/defendpoint GET "/dashboard/:uuid/card/:card-id"
  "Fetch the results for a Card in a publically-accessible Dashboard. Does not require auth credentials. Public sharing must be enabled."
  [uuid card-id parameters]
  {parameters (s/maybe su/JSONString)}
  (api/check-public-sharing-enabled)
  (api/check-exists? DashboardCard :dashboard_id (uuid->dashboard-id uuid), :card_id card-id)
  (run-query-for-card-returning-results-in-format card-id parameters))


(api/define-routes)
