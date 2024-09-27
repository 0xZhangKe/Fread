import FreadKit 
import SwiftUI

class AppDelegate : UIResponder, UIApplicationDelegate {

    lazy var applicationCompoent: IosApplicationComponent = createApplicationComponent(
        appDelegate: self
    )

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

        return true
    }
}

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    var body: some Scene {
        WindowGroup {
            let activityComonent = createActivityComponent(
                applicationComponent: delegate.applicationCompoent
            )
            ContentView(component: activityComonent)
        }
    }
}

private func createApplicationComponent(
    appDelegate: AppDelegate
) -> IosApplicationComponent {
    return IosApplicationComponent.companion.create(
        applicationDelegate: appDelegate
    )
}

private func createActivityComponent(
    applicationComponent: IosApplicationComponent
) -> IosActivityComponent {
    return IosActivityComponent.companion.create(
        applicationComponent: applicationComponent
    )
}
