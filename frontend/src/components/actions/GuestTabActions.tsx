import {DisplayGuestTabItem, GuestTabStatus} from "@/model/Interfaces";
import {CloseGuestTabDialog} from "@/components/dialog/CloseGuestTabDialog";
import {RegisterPaymentDialog} from "@/components/dialog/RegisterPaymentDialog";
import React from "react";

interface GuestTabActionsProps {
    guestTab: DisplayGuestTabItem;
}

export const GuestTabActions: React.FC<GuestTabActionsProps> = ({ guestTab }) => {
    switch (guestTab.status) {
        case GuestTabStatus.OPEN.value:
            return <CloseGuestTabDialog guestTab={guestTab} />;
        case GuestTabStatus.CLOSED.value:
            return <RegisterPaymentDialog guestTab={guestTab} />;
        default:
            return null;
    }
};
