import UIKit
import FreadKit
import SwiftUI

struct ContentView: View {

    private let component: IosActivityComponent

    init(component: IosActivityComponent) {
        self.component = component
    }

    var body: some View {
        ComposeView(component: self.component)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}

struct ComposeView: UIViewControllerRepresentable {

    private let component: IosActivityComponent

    init(component: IosActivityComponent) {
        self.component = component
    }

    func makeUIViewController(context: Context) -> UIViewController {
        component.uiViewControllerFactory()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}





