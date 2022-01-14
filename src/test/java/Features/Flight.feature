Feature: Access Goibibo and search through flights

Scenario Outline: Access Goibibo and search through flights
Given the user navigates to website homepage using data from spreadsheet row <Row>
And an option from is selected for One-Way, Roundtrip or Multi-City
And a starting and final destination are entered
And a departure and return date are selected
And the user selects the number of travelers and travel class
And the user selects the Search button
Then the flight selection page should be displayed
  Examples:
    |Row|
    |0|
    |1|
    |2|
    |3|