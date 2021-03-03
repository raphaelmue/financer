import React from 'react';

const LightTheme = React.lazy(() => import('./light.theme'));
const DarkTheme = React.lazy(() => import('./dark.theme'));

export enum Theme {
    DARK = 'dark',
    LIGHT = 'light'
}

interface ThemeProviderComponentProps {
    theme: Theme
}

interface ThemeProviderComponentState {
}

class ThemeProvider extends React.Component<ThemeProviderComponentProps, ThemeProviderComponentState> {

    render() {
        return <div>
            <React.Suspense fallback={<></>}>
                {(this.props.theme === Theme.LIGHT) && <LightTheme/>}
                {(this.props.theme === Theme.DARK) && <DarkTheme/>}
            </React.Suspense>
            {this.props.children}
        </div>;
    }
}

export default ThemeProvider;


