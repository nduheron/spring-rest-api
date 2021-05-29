Feature: cache operations

  Scenario: search all caches
    Given cache "test" with values:
      | key1 | value1 |
      | key2 | value2 |
    Given cache "test2" with values:
      | key1 | value1 |
      | key2 | value3 |
    When I search all caches
    Then I get a OK response
    And I find caches "test,test2"

  Scenario: flush all caches
    Given cache "test" with values:
      | key1 | value1 |
      | key2 | value2 |
    Given cache "test2" with values:
      | key1 | value1 |
      | key2 | value3 |
    When I flush all caches
    Then I get a NO_CONTENT response
    And all caches are empty

  Scenario: flush unknown cache
    When I flush cache "unknown"
    Then I get a NO_CONTENT response
    And cache "unknown" is empty

  Scenario: flush existing cache
    Given cache "test" with values:
      | key1 | value1 |
      | key2 | value2 |
    When I flush cache "test"
    Then I get a NO_CONTENT response
    And cache "test" is empty

  Scenario: disabled cache
    Given cache "testdisable" with values:
      | key1 | value1 |
      | key2 | value2 |
    When I search cache "testdisable"
    Then I get a NOT_FOUND response

  Scenario: unknown cache
    When I search cache "unknown"
    Then I get a OK response
    And I don't find values

  Scenario: search cache content
    Given cache "test" with values:
      | key1 | value1 |
      | key2 | value2 |
    When I search cache "test"
    Then I get a OK response
    And I find values:
      | key1 | value1 |
      | key2 | value2 |