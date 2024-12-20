import { useQueryWithCallbacks } from "./fetchCacheApi";
import { fetchAccountGroupList } from "../api/mypantry/account/accountService";

const ACCOUNT_GROUP_KEY = 'accountGroup';
const ACCOUNT_GROUP_OPTIONS_KEY = 'accountGroupOptions';

export function useGetAccountGroups({ email }, callbacks = {}) {

    return useQueryWithCallbacks(
        [ACCOUNT_GROUP_KEY, email],
        ({ signal }) => fetchAccountGroupList(signal),
        { enabled: !!email },
        callbacks
    );
}

export function useGetAccountGroupsOptions({ email, accountGroups }, callbacks = {}) {

    const queryFn = () => {
        var list = [];
        accountGroups.map((group) => {
            return list = [...list,
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