package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.DescribeBudgetActionsForAccountRequest;
import software.amazon.awssdk.services.budgets.model.DescribeBudgetActionsForAccountResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListHandler extends BudgetsBaseHandler<CallbackContext> {

    public ListHandler() {
        super();
    }

    public ListHandler(BudgetsClient budgetsClient) {
        super(budgetsClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final List<ResourceModel> models = new ArrayList<>();
        String nextToken = null;
        do {
            DescribeBudgetActionsForAccountResponse paginatedResult = proxy.injectCredentialsAndInvokeV2(
                    buildListRequest(request,nextToken),
                    budgetsClient::describeBudgetActionsForAccount);
            nextToken = paginatedResult.nextToken();
            models.addAll(paginatedResult.actions().stream()
                    .map(Utils::convertActionFromSdk)
                    .collect(Collectors.toList()));
        }while (nextToken != null);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(models)
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private DescribeBudgetActionsForAccountRequest buildListRequest(ResourceHandlerRequest<ResourceModel> request,
                                                                    String nextToken) {
        return DescribeBudgetActionsForAccountRequest.builder()
                .accountId(request.getAwsAccountId())
                .nextToken(nextToken)
                .build();
    }
}
