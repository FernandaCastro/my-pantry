import { getAccountGroupList } from "../api/mypantry/account/accountService";
import { useQueryWithCallbacks } from "./useQueryWithCallbacks";

const ACCOUNT_GROUP_KEY = 'accountGroup';
const ACCOUNT_GROUP_OPTIONS_KEY = 'accountGroupOptions';

export function useGetAccountGroups({ email }, callbacks = {}) {

    return useQueryWithCallbacks(
        [ACCOUNT_GROUP_KEY, email],
        ({ signal }) => getAccountGroupList(signal),
        { enabled: !!email },
        callbacks
    );
}

export function useGetAccountGroupsOptions({ email, accountGroups }, callbacks = {}) {

    const queryFn = () => {
        var list = [];
        accountGroups.map((group) => {
            list = [...list,
            {
                value: group.id,
                label: group.name
            }]
        })
        return list;
    }

    return useQueryWithCallbacks(
        [ACCOUNT_GROUP_OPTIONS_KEY, email],
        queryFn,
        { enabled: (!!email && !!accountGroups) },
        callbacks
    );
}