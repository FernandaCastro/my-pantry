import useGlobalLoading from "../state/useLoading";
import { Loading } from "./Loading";

export default function GlobalLoading() {

    const { isLoading } = useGlobalLoading();

    return (
        <>
            {(isLoading === true) && <Loading />}
        </>
    )
}