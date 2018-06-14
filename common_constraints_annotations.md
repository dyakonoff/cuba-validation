## List of constraint annotations supported by CUBA studio

| Annotation    | Types (Standard for CUBA entities) | Note  |
|---------------|------------------------------------|-------|
| @NotNull      | All                                | The annotated element must not be null. |
| @Length       | String                             | Validate that the string is between min and max included. |
| @Pattern      | String                             | The annotated String must match the specified regular expression. |
| @Email        | String                             | Checks that the string has to be a well-formed email address. |
| @Min          | Integer, Long                      | A number whose value must be higher or equal to the specified minimum. |
| @Max          | Integer, Long                      | A number whose value must be lower or equal to the specified maximum. |
| @Digits       | BigDecimal, Double, Integer, Long  | Sets the max number of integer and fractional digits of the number. |
| @DecimalMin   | BigDecimal, Double                 | Same as @Min but you can specify fractional part and set `inclusive` flag. |
| @DecimalMax   | BigDecimal, Double                 | Same as @Max but you can specify fractional part and set `inclusive` flag. |
| @Past         | Date, DateTime                     | Element must be a date in the past. |
| @Future       | Date, DateTime                     | Element must be a date in the future. |

## Other useful annotations

| Annotation    | Types                                 | Note  |
|---------------|---------------------------------------|-------|
| @Size         | CharSequence, Collection , Map, array | The annotated element size must be between the specified boundaries (included). |
| @Valid        | Reference fields                      | Marks reference field for cascade validation. [Documentation.](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_related_objects) |
| @RequiredView | Service method and it's parameters    | Ensures that entity instances are loaded with all the attributes specified in the view. [Documentation.](https://doc.cuba-platform.com/manual-6.9/bean_validation_constraints.html#bean_validation_cuba_annotations) |
| @Validated    | Middleware services methods           | Marks middleware methods parameters and return data for validation. See [documentation](https://doc.cuba-platform.com/manual-6.9/bean_validation_running.html#bean_validation_in_services) for details. |

[Back to the article](README.md)