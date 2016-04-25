     _            _  _ _
    | |___      _(_)(_|_) ___
    | __\ \ /\ / / || | |/ _ \
    | |_ \ V  V /| || | | (_) |
     \__| \_/\_/ |_|/ |_|\___/
                  |__/

Pure Clojure library for interacting with Twilio's REST API

## Usage

This library can be used with the http client of your choice, or using the built-in http-kit integration.

### Hello World

    (require '(twijio.http :refer [twilio-request!])
              (twijio.api :refer [send-message]))

    (def config
      {:twilio-account "your-twilio-account-sid"
       :twilio-token "tour-twilio-token"})

    (twilio/send-message config {:to "+15555555555" :from "+12345678901" :body "Hello World!"})

## Run Tests

Tests employ Twilio (test credentials)[https://www.twilio.com/docs/api/rest/test-credentials].
Use your own test credentials in order to run the tests.

    TWILIO_TEST_ACCOUNT=<twilio-test-sid>
    TWILIO_TEST_TOKEN=<twilio-test-token>
    lein test

## Contributing

Make a PR! Go for it. You know you want to.

## License

Copyright © 2016 OpenTable

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
