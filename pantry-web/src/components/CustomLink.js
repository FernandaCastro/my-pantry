import { Link } from "react-router-dom"

export default function CustomLink({ to, children, bsPrefix, className, onClick }) {
    return (
        <div className={bsPrefix} onClick={onClick}>
            <Link to={to} className={className} style={{ textDecoration: 'none', color: 'inherit' }}>
                {children}
            </Link>
        </div>
    )
}