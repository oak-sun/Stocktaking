package nam.gor.stocktaking.infrastucture.util;

public class StringPatterns {
   public   static final String TASKMASTER_URL = "/api/v1/taskmasters";
   public   static final String STOCK_KEEPER_URL = "/api/v1/stockkeepers";
   public static final String EQUIPMENT_URL = "/api/v1/equipments";
   public static final String MACHINERY_URL = "/api/v1/machines";
   public static final String STOCK_URL = "/api/v1/stock";
   public static final String ERROR_BODY_MESSAGE =
           "There's been an unexpected error. Please, contact support.";

  public static final String ERROR_VALIDATION =
          "There were validation errors in the request.";

  public static final String ERROR_UNKNOWN_ENTITY =
          "An attempt to operate on an unknown entity was made.";

  public static final String UNEXPECTED_ERROR =
          "An unexpected error was thrown when performing the request: ";

 public static final String INVALID_PAYLOAD =
  "The given payload is invalid. Check the 'details' field.";

    public static final String TASKMASTER_NOT_FOUND =
            "TaskMaster_Out_DTO with ID %s was not found";
    public static final String STOCK_KEEPER_NOT_FOUND =
            "StockKeeperOutDTO with ID %s was not found";

    public static final String MACHINERY_NOT_FOUND =
            "MachineryOutDTO with ID %s was not found";

    public static final String EQUIPMENT_NOT_FOUND =
            "Equipment_Out_DTO with ID %s was not found";
}
