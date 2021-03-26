package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.BudgetsClient;
import software.amazon.awssdk.services.budgets.model.DescribeBudgetActionRequest;
import software.amazon.awssdk.services.budgets.model.DescribeBudgetActionResponse;
import software.amazon.awssdk.services.budgets.model.NotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.budgets.budgetsaction.Utils.convertActionFromSdk;

public class ReadHandler extends BudgetsBaseHandler<CallbackContext> {

    public ReadHandler() {
        super();
    }

    public ReadHandler(BudgetsClient budgetsClient) {
        super(budgetsClient);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        DescribeBudgetActionResponse readResult = null;
        try {
            readResult = proxy.injectCredentialsAndInvokeV2(
                    buildReadRequest(model, request),
                    budgetsClient::describeBudgetAction);
        } catch (NotFoundException ex) {
            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                    .status(OperationStatus.FAILED)
                    .errorCode(HandlerErrorCode.NotFound)
                    .message(String.format("Action %s does not exist.", model.getActionId()))
                    .build();
        }

        final ResourceModel outputModel = convertActionFromSdk(readResult.action());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(outputModel)
                .status(OperationStatus.SUCCESS)
                .build();
    }

    public DescribeBudgetActionRequest buildReadRequest(ResourceModel model,
                                                        ResourceHandlerRequest<ResourceModel> request) {
        return DescribeBudgetActionRequest.builder()
                .accountId(request.getAwsAccountId())
                .actionId(model.getActionId())
                .budgetName(model.getBudgetName())
                .build();
    }
}
