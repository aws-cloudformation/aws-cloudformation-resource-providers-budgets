package software.amazon.budgets.budgetsaction;

import software.amazon.awssdk.services.budgets.model.Action;
import software.amazon.awssdk.services.budgets.model.ActionThreshold;
import software.amazon.awssdk.services.budgets.model.Definition;
import software.amazon.awssdk.services.budgets.model.IamActionDefinition;
import software.amazon.awssdk.services.budgets.model.ScpActionDefinition;
import software.amazon.awssdk.services.budgets.model.SsmActionDefinition;
import software.amazon.awssdk.services.budgets.model.Subscriber;

import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static Definition convertDefinitionFromCfn(software.amazon.budgets.budgetsaction.Definition cfnDefinition) {
        Definition.Builder sdkDefinitionBuilder = Definition.builder();
        if (cfnDefinition.getIamActionDefinition() != null) {
            IamActionDefinition iamActionDefinition = IamActionDefinition.builder()
                    .groups(cfnDefinition.getIamActionDefinition().getGroups())
                    .users(cfnDefinition.getIamActionDefinition().getUsers())
                    .roles(cfnDefinition.getIamActionDefinition().getRoles())
                    .policyArn(cfnDefinition.getIamActionDefinition().getPolicyArn())
                    .build();
            sdkDefinitionBuilder.iamActionDefinition(iamActionDefinition);
        }
        if (cfnDefinition.getScpActionDefinition() != null) {
            ScpActionDefinition scpActionDefinition = ScpActionDefinition.builder()
                    .policyId(cfnDefinition.getScpActionDefinition().getPolicyId())
                    .targetIds(cfnDefinition.getScpActionDefinition().getTargetIds())
                    .build();
            sdkDefinitionBuilder.scpActionDefinition(scpActionDefinition);
        }
        if (cfnDefinition.getSsmActionDefinition() != null) {
            SsmActionDefinition ssmActionDefinition = SsmActionDefinition.builder()
                    .actionSubType(cfnDefinition.getSsmActionDefinition().getSubtype())
                    .region(cfnDefinition.getSsmActionDefinition().getRegion())
                    .instanceIds(cfnDefinition.getSsmActionDefinition().getInstanceIds())
                    .build();
            sdkDefinitionBuilder.ssmActionDefinition(ssmActionDefinition);
        }
        return sdkDefinitionBuilder.build();
    }

    public static List<Subscriber> convertSubscribersFromCfn(List<software.amazon.budgets.budgetsaction.Subscriber>
                                                                     cfnSubscribers) {
        if (cfnSubscribers == null) {
            return null;
        }
        return cfnSubscribers.stream()
                .map( s -> Subscriber.builder().subscriptionType(s.getType()).address(s.getAddress()).build())
                .collect(Collectors.toList());
    }

    public static ActionThreshold convertActionThresholdFromCfn(software.amazon.budgets.budgetsaction.ActionThreshold
                                                                        cfnActionThreshold) {
        return ActionThreshold.builder()
                .actionThresholdType(cfnActionThreshold.getType())
                .actionThresholdValue(cfnActionThreshold.getValue())
                .build();
    }

    public static ResourceModel convertActionFromSdk(Action sdkAction) {
        ResourceModel cfnAction = new ResourceModel();
        cfnAction.setActionId(sdkAction.actionId());
        cfnAction.setActionThreshold(convertActionThresholdFromSdk(sdkAction.actionThreshold()));
        cfnAction.setActionType(sdkAction.actionTypeAsString());
        cfnAction.setApprovalModel(sdkAction.approvalModelAsString());
        cfnAction.setBudgetName(sdkAction.budgetName());
        cfnAction.setDefinition(convertDefinitionFromSdk(sdkAction.definition()));
        cfnAction.setExecutionRoleArn(sdkAction.executionRoleArn());
        cfnAction.setNotificationType(sdkAction.notificationTypeAsString());
        if(sdkAction.subscribers() != null) {
            cfnAction.setSubscribers(convertSubscribersFromSdk(sdkAction.subscribers()));
        }

        return cfnAction;
    }

    public static software.amazon.budgets.budgetsaction.Definition convertDefinitionFromSdk(Definition sdkDefinition) {
        software.amazon.budgets.budgetsaction.Definition cfnDefinition =
                new software.amazon.budgets.budgetsaction.Definition();
        if (sdkDefinition.iamActionDefinition() != null) {
            software.amazon.budgets.budgetsaction.IamActionDefinition iamActionDefinition =
                    new software.amazon.budgets.budgetsaction.IamActionDefinition();
            iamActionDefinition.setGroups(sdkDefinition.iamActionDefinition().groups());
            iamActionDefinition.setUsers(sdkDefinition.iamActionDefinition().users());
            iamActionDefinition.setRoles(sdkDefinition.iamActionDefinition().roles());
            iamActionDefinition.setPolicyArn(sdkDefinition.iamActionDefinition().policyArn());
            cfnDefinition.setIamActionDefinition(iamActionDefinition);
        }
        if (sdkDefinition.scpActionDefinition() != null) {
            software.amazon.budgets.budgetsaction.ScpActionDefinition scpActionDefinition =
                    new software.amazon.budgets.budgetsaction.ScpActionDefinition();
            scpActionDefinition.setPolicyId(sdkDefinition.scpActionDefinition().policyId());
            scpActionDefinition.setTargetIds(sdkDefinition.scpActionDefinition().targetIds());
            cfnDefinition.setScpActionDefinition(scpActionDefinition);
        }
        if (sdkDefinition.ssmActionDefinition() != null) {
            software.amazon.budgets.budgetsaction.SsmActionDefinition ssmActionDefinition =
                    new software.amazon.budgets.budgetsaction.SsmActionDefinition();
            ssmActionDefinition.setSubtype(sdkDefinition.ssmActionDefinition().actionSubTypeAsString());
            ssmActionDefinition.setRegion(sdkDefinition.ssmActionDefinition().region());
            ssmActionDefinition.setInstanceIds(sdkDefinition.ssmActionDefinition().instanceIds());
            cfnDefinition.setSsmActionDefinition(ssmActionDefinition);
        }

        return cfnDefinition;
    }

    public static software.amazon.budgets.budgetsaction.ActionThreshold convertActionThresholdFromSdk(
            ActionThreshold sdkActionThreshold) {
        return software.amazon.budgets.budgetsaction.ActionThreshold.builder()
                .type(sdkActionThreshold.actionThresholdTypeAsString())
                .value(sdkActionThreshold.actionThresholdValue())
                .build();
    }

    public static List<software.amazon.budgets.budgetsaction.Subscriber> convertSubscribersFromSdk(
            List<Subscriber> sdkSubscribers) {
        if (sdkSubscribers == null) {
            return null;
        }
        return sdkSubscribers.stream()
                .map(s -> software.amazon.budgets.budgetsaction.Subscriber.builder()
                        .type(s.subscriptionTypeAsString())
                        .address(s.address())
                        .build())
                .collect(Collectors.toList());
    }
}
