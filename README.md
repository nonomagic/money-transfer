# Test task for Revolut

```
% make test
% make run
```

## Requirements
### Explicit requirements:

1. keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is invoked by another internal system/service)
2. use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the
requirement #1
3. the datastore should run in-memory for the sake of this test
4. the final result should be executable as a standalone program (should not require a pre-installed
container/server)
5. demonstrate with tests that the API works as expected

### Implicit requirements:
1. the code produced by you is expected to be of high quality.
2. there are no detailed requirements, use common sense.

## Implementation

* Long story short, there're two main resources available through the API: account owners and accounts. Account owners may own zero or more accounts.
* All non-atomic data manipulation operations are hidden behind the DAO layer, so no explicit assumptions are made about concurrency level of the server or atomicity of the data layer.
* I gave up on writing high-level REST API tests: it's way too painful and time-consuming if your only option is Java (otherwise I'd use python or js).

## API

If you fancy using [Insomnia](https://support.insomnia.rest/), feel free to plug in `Insomnia.json` configuration I left in the repo, it should make it easier to play around with the API.

## /ping

* `GET /ping` - healthcheck

## /owners

* `POST /owners` - create an account owner
```
{
   "name": <name>, # String
   "email": <email> # String (for simplicity I don't verify if the value looks like an email)
}
```
* `GET /owners` - list all the owners
* `GET /owners/:id` - get an account owner by its id
* `GET /owners/:id/accounts` - list all the accounts of the owner
* `DELETE /owners/:id` - delete an account owner (only possible if it has no accounts)

## /accounts

* `POST /accounts` - create an account
```
{
   "ownerId": <ownerId>, # Stirng
   "balance": <initial-balance> # Double
}
```
* `GET /accounts` - list all accounts
* `GET /accounts/:id` - get an account by its id
* `PUT /accounts/:from_id/transfer/:to_id` - transfer a requested amount from account `:from_id` to `:to_id` (shall not be the same)
```
{
   "amount": <amount> # Double
}
```
* `DELETE /accounts/:id` - delete an account
