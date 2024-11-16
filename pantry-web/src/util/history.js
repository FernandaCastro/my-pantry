export const history = {
    navigate: null,
    push: (page, ...rest) => history.navigate(page, ...rest),
}