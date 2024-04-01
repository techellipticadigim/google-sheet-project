import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;


public class GoogleSheetsExampleServiceAccount {
    private static Sheets sheetsService;
    private static final String APPLICATION_NAME = "Google Sheets O-Auth Example";
    private static final List<String> AUTH_SCOPES = List.of(SheetsScopes.SPREADSHEETS);
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static  ServiceAccountCredentials credentials;
    private static final String SPREADSHEET_ID = "1ZAV9nsx40EZsmPr_xlYDqYBsl285hpMp0s1VKA1IBzY";

    public static void getSheetsService() throws IOException, GeneralSecurityException {
        if (sheetsService == null) {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            sheetsService = new Sheets.Builder(httpTransport, JSON_FACTORY, request -> {
            })
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }

    public static void getCredentials() throws Exception {
         getSheetsService();
        if (credentials != null) {
            if (credentials.getAccessToken().getExpirationTime().before(new Date())) {
                credentials.refresh();
            }
            return;
        }
        try (InputStream in = GoogleSheetsExampleServiceAccount.class.getResourceAsStream("/service-account-auth.json")) {
            credentials = (ServiceAccountCredentials) ServiceAccountCredentials
                    .fromStream(Objects.requireNonNull(GoogleSheetsExampleServiceAccount.class.getResourceAsStream("/service-account-auth.json")))
                    .createScoped(AUTH_SCOPES);
            credentials.refresh();
        }
    }

        public static void main(String...args)throws Exception{
        List<String> ranges = Arrays.asList("Sheet1!A1:A4","Sheet1!B1:B4");
             getCredentials();
        BatchGetValuesResponse readResult = sheetsService.spreadsheets().values()
                .batchGet(SPREADSHEET_ID)
                .setRanges(ranges)
                .setAccessToken(credentials.getAccessToken().getTokenValue())
                .execute();

        ValueRange firstColumn = readResult.getValueRanges().get(0);
        for(List<Object> values : firstColumn.getValues()){
            for(Object value : values){
                System.out.println(value.toString());
            }
        }

        ValueRange secondColumn = readResult.getValueRanges().get(1);
        for(List<Object> values : secondColumn.getValues()){
            for(Object value : values){
                System.out.println(value.toString());
            }
        }

    }
}
