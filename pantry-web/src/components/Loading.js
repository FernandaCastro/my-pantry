
export const Loading = () => {

    const RippleOverlayLoading = () => {
        return (
            <div className="loading-overlay">
                <div className="independent-ripple"></div>
            </div>
        )
    }
    
    const RippleLoading = () => {
        return (
            <div className="loader-container" >
                <div className="ripple"></div>
            </div>
        )
    }

    return <RippleOverlayLoading />
}