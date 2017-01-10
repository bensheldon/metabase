(ns metabase.api.public-test
  "Tests for `api/public/` endpoints."
  (:require [cheshire.core :as json]
            [expectations :refer :all]
            [metabase.http-client :as http]
            (metabase.models [card :refer [Card]]
                             [dashboard :refer [Dashboard]]
                             [public-card :refer [PublicCard]]
                             [public-dashboard :refer [PublicDashboard]])
            metabase.public-settings ; for `enable-public-sharing
            [metabase.query-processor-test :as qp-test]
            [metabase.test.data :as data]
            [metabase.test.data.users :as test-users]
            [metabase.test.util :as tu]
            [metabase.util :as u])
  (:import java.util.UUID))

(defn- count-of-venues-card []
  {:dataset_query {:database (data/id)
                   :type     :query
                   :query    {:source_table (data/id :venues)
                              :aggregation  [:count]}}})

(defmacro ^:private with-temp-public-card {:style/indent 1} [[binding & [card]] & body]
  `(tu/with-temp* [Card       [card#    (merge (count-of-venues-card) ~card)]
                   PublicCard [~binding {:card_id (u/get-id card#)}]]
     ~@body))

;;; ------------------------------------------------------------ POST /api/public/card/:uuid ------------------------------------------------------------

;; Check that we *cannot* execute a PublicCard if the setting is disabled
(expect
  "Public sharing is not enabled."
  (tu/with-temporary-setting-values [enable-public-sharing false]
    (with-temp-public-card [{uuid :uuid}]
      (http/client :post 400 (str "public/card/" uuid)))))

;; Check that we get a 404 if the PublicCard doesn't exist
(expect
  "Not found."
  (tu/with-temporary-setting-values [enable-public-sharing true]
    (http/client :post 404 (str "public/card/" (UUID/randomUUID)))))

;; Check that we *cannot* execute a PublicCard if the Card has been archived
(expect
  "Not found."
  (tu/with-temporary-setting-values [enable-public-sharing true]
    (with-temp-public-card [{uuid :uuid} {:archived true}]
      (http/client :post 404 (str "public/card/" uuid)))))

;; Check that we can exec a PublicCard
(expect
  [[100]]
  (tu/with-temporary-setting-values [enable-public-sharing true]
    (with-temp-public-card [{uuid :uuid}]
      (qp-test/rows (http/client :post 200 (str "public/card/" uuid))))))

;; Check that we can exec a PublicCard with `?format=json`
(expect
  (tu/with-temporary-setting-values [enable-public-sharing true]
    (with-temp-public-card [{uuid :uuid}]
      (http/client :post 200 (str "public/card/" uuid), :format :json))))

;; Check that we can exec a PublicCard with `?format=csv`
(expect
  "count\n100\n"
  (tu/with-temporary-setting-values [enable-public-sharing true]
    (with-temp-public-card [{uuid :uuid}]
      (http/client :post 200 (str "public/card/" uuid), :format :csv))))

;; Check that we can exec a PublicCard with `?parameters`
(expect
  [{:type "category", :value 2}]
  (tu/with-temporary-setting-values [enable-public-sharing true]
    (with-temp-public-card [{uuid :uuid}]
      (get-in (http/client :post 200 (str "public/card/" uuid), :parameters (json/encode [{:type "category", :value 2}]))
              [:json_query :parameters]))))


;;; ------------------------------------------------------------ GET /api/public/dashboard/:uuid ------------------------------------------------------------

;; TODO Check that we *cannot* fetch PublicDashboard if setting is disabled

;; TODO Check that we get a 404 if the PublicDashboard doesn't exist

;; TODO Check that we don't see Cards that have been archived

;; TODO Check that we can fetch a PublicDashboard


;;; ------------------------------------------------------------ GET /api/public/dashboard/:uuid/card/:card-id ------------------------------------------------------------

;; TODO Check that we *cannot* exec PublicCard via PublicDashboard if setting is disabled

;; TODO Check that we get a 404 if PublicDashboard doesn't exist

;; TODO Check that we get a 404 if PublicCard doesn't exist

;; TODO Check that we *cannot* execute a PublicCard via a PublicDashboard if the Card has been archived

;; TODO Check that we can exec a PublicCard via a PublicDashboard

;; TODO Check that we can exec a PublicCard via a PublicDashboard with `?parameters`
