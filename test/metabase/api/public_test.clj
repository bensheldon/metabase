(ns metabase.api.public-test
  "Tests for `api/public/` endpoints."
  (:require [expectations :refer :all]
            (metabase.models [card :refer [Card]]
                             [dashboard :refer [Dashboard]]
                             [public-card :refer [PublicCard]]
                             [public-dashboard :refer [PublicDashboard]])
            [metabase.test.data.users :as test-users]
            [metabase.test.util :as tu]
            [metabase.util :as u]))

;;; ------------------------------------------------------------ POST /api/public/card/:uuid ------------------------------------------------------------

;; TODO Check that we *cannot* execute a PublicCard if the setting is disabled

;; TODO Check that we get a 404 if the PublicCard doesn't exist

;; TODO Check that we *cannot* execute a PublicCard if the Card has been archived

;; TODO Check that we can exec a PublicCard

;; TODO Check that we can exec a PublicCard with `?format=json`

;; TODO Check that we can exec a PublicCard with `?format=csv`

;; TODO Check that we can exec a PublicCard with `?parameters`


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
