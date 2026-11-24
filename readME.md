# Gift to go test

## List of tasks
- add to integration test suite
- remove boiler plate from work item
- yml changes
- global exception handling
- validation of create request
- status Enum transition logic
- enhance controller
- patch status functionality
- filtering sorting pagination functionality

## Future changes / time limit constraints
- Complete the filtering sorting pagination by verifying it by replacing the list endpoint with a pagination one where Pageable and the filter fields are query parameters calling the paginationList method in the WorkItemService. Add default pagination parameters as env variables to application yml (default page size and maximum page size). Add filtering for createdAt / updatedAt on between once basic string filtering is working E2E
- Add rate limiting to the API's using resilience4j-spring-boot3. Add default behavior through env variables to the application yml. Create separate integration test suite with either overridden properties or test profile to verify. Return 429 in this scenario
- Add an intermediary model to the service (WorkItemModel) to decouple the entity from business logic and ensure API DTO's stay only in controllers. This coupling made unit testing difficult which cost time as we do not want to mutate data in these objects. Decoupling also simplifies any database migrations in the future as changes would only need to happen in its mapper. Move the transitionTo logic to the model
- Use mapstructs to generate mapper classes to reduce boiler plate code
- Tidy up testing to make them more readable and add more for maximum coverage on pre-existing code. Revisit all to add more appropriate assertions - fields and specific error messages where required
- Add appropriate logging once the solution is complete. Add debug at the start / end of each endpoint and warnings on the exception cases
- Remove the touch call in workitem and look to do the update on the database side either with a database trigger or part of the table definition if available
- Add idempotency to the create / patch apis through an id in the header to prevent a client from sending the same request multiple times. If request is still in progress return 409, if it was completed return cached response and 200