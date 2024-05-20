import { Link } from 'react-router-dom';

export default function Welcome() {

    return (
        <h6 className="mt-3 title"><br /> Welcome! <br /> <br /> Please <Link to={`/login`} >log in</Link> to continue...</h6>
    )
}