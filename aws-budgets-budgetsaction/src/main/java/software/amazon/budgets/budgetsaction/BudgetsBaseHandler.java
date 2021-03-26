package software.amazon.budgets.budgetsaction;


import software.amazon.awssdk.core.internal.http.loader.DefaultSdkHttpClientBuilder;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.ImmutableMap;

import java.time.Duration;
import java.util.Map;

public abstract class BudgetsBaseHandler<CallBackT> extends BaseHandler<CallbackContext> {

    protected final BudgetsClient budgetsClient;

    private static final Duration HTTP_READ_TIMEOUT = Duration.ofSeconds(65);

    //Budgets is global in partition. Thus, in order to choose the global region when constructing the client,
    //we need to have this map from partition to global region
    protected static Map<String, Region> partitionToGlobalRegionMap = ImmutableMap.of(
            Region.CN_NORTH_1.metadata().partition().name(), Region.AWS_CN_GLOBAL,
            Region.US_GOV_EAST_1.metadata().partition().name(), Region.AWS_US_GOV_GLOBAL,
            Region.US_ISO_EAST_1.metadata().partition().name(), Region.AWS_ISO_GLOBAL,
            Region.US_ISOB_EAST_1.metadata().partition().name(), Region.AWS_ISO_B_GLOBAL,
            Region.US_EAST_1.metadata().partition().name(), Region.AWS_GLOBAL
    );

    public BudgetsBaseHandler() {
        AttributeMap httpOptions = AttributeMap.builder()
                .put(SdkHttpConfigurationOption.READ_TIMEOUT, HTTP_READ_TIMEOUT)
                .build();
        this.budgetsClient = BudgetsClient.builder()
                .httpClient(new DefaultSdkHttpClientBuilder().buildWithDefaults(httpOptions))
                .region(partitionToGlobalRegionMap.get(
                        Region.of(System.getenv("AWS_REGION")).metadata().partition().name()))
                .build();
    }

    public BudgetsBaseHandler(BudgetsClient budgetsClient) {
        this.budgetsClient = budgetsClient;
    }

}
