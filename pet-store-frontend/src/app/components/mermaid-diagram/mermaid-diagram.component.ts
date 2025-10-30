import {
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild,
  AfterViewInit,
  OnDestroy,
  Inject,
  PLATFORM_ID,
  OnChanges,
  SimpleChanges,
} from "@angular/core";
import { CommonModule, isPlatformBrowser } from "@angular/common";

declare global {
  interface Window {
    mermaid: any;
  }
}

@Component({
  selector: "app-mermaid-diagram",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./mermaid-diagram.component.html",
  styleUrls: ["./mermaid-diagram.component.scss"],
})
export class MermaidDiagramComponent
  implements OnInit, AfterViewInit, OnDestroy, OnChanges
{
  @ViewChild("mermaidDiv", { static: true }) mermaidDiv!: ElementRef;

  @Input() diagramDefinition: string = "";
  @Input() title: string = "";
  @Input() description: string = "";
  @Input() showActions: boolean = true;
  @Input() theme: "default" | "dark" | "forest" | "neutral" = "default";

  private static renderCounter = 0;
  private static renderQueue: (() => Promise<void>)[] = [];
  private static isProcessingQueue = false;
  private mermaidInitialized: boolean = false;
  private mermaidLoaded: boolean = false;

  diagramId: string = "";
  isFullscreen: boolean = false;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  ngOnInit() {
    this.diagramId = "mermaid-" + Math.random().toString(36).substr(2, 9);
    if (isPlatformBrowser(this.platformId)) {
      this.loadMermaidScript();
    }
  }

  ngAfterViewInit() {
    this.queueDiagramRender();
  }

  private queueDiagramRender() {
    const renderFunction = async () => {
      if (this.mermaidLoaded && this.mermaidDiv?.nativeElement) {
        if (this.mermaidDiv.nativeElement.offsetParent !== null) {
          await this.renderDiagram();
        } else {
          await new Promise((resolve) => setTimeout(resolve, 200));
          if (this.mermaidLoaded && this.mermaidDiv?.nativeElement) {
            await this.renderDiagram();
          }
        }
      }
    };

    MermaidDiagramComponent.renderQueue.push(renderFunction);
    MermaidDiagramComponent.processRenderQueue();
  }

  private static async processRenderQueue() {
    if (
      MermaidDiagramComponent.isProcessingQueue ||
      MermaidDiagramComponent.renderQueue.length === 0
    ) {
      return;
    }

    MermaidDiagramComponent.isProcessingQueue = true;

    while (MermaidDiagramComponent.renderQueue.length > 0) {
      const renderFunction = MermaidDiagramComponent.renderQueue.shift();
      if (renderFunction) {
        try {
          await renderFunction();
          await new Promise((resolve) => setTimeout(resolve, 250));
        } catch (error) {
          console.error("Error in queued diagram render:", error);
        }
      }
    }

    MermaidDiagramComponent.isProcessingQueue = false;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (
      changes["diagramDefinition"] &&
      !changes["diagramDefinition"].firstChange
    ) {
      if (this.mermaidLoaded) {
        this.renderDiagram();
      }
    }
  }

  ngOnDestroy() {
    if (this.isFullscreen) {
      this.toggleFullscreen();
    }
  }

  private loadMermaidScript() {
    if (window.mermaid) {
      this.mermaidLoaded = true;
      this.initializeMermaid();
      this.renderDiagram();
      return;
    }

    this.loadScriptFromCDN(
      "https://cdn.jsdelivr.net/npm/mermaid@10.9.1/dist/mermaid.min.js"
    )
      .catch(() => {
        console.warn("Primary Mermaid CDN failed, trying fallback...");
        return this.loadScriptFromCDN(
          "https://unpkg.com/mermaid@10.9.1/dist/mermaid.min.js"
        );
      })
      .catch(() => {
        console.error("All Mermaid CDNs failed");
        this.showErrorMessage(
          "Failed to load diagram library from CDN. Please check your internet connection."
        );
      });
  }

  private loadScriptFromCDN(src: string): Promise<void> {
    return new Promise((resolve, reject) => {
      const script = document.createElement("script");
      script.src = src;
      script.onload = () => {
        this.mermaidLoaded = true;
        this.initializeMermaid();
        this.renderDiagram();
        resolve();
      };
      script.onerror = () => {
        reject(new Error(`Failed to load script from ${src}`));
      };
      document.head.appendChild(script);
    });
  }

  private initializeMermaid() {
    if (!this.mermaidInitialized && window.mermaid) {
      try {
        if (window.mermaid.mermaidAPI) {
          window.mermaid.mermaidAPI.reset();
        }

        window.mermaid.initialize({
          startOnLoad: false,
          theme: this.theme,
          securityLevel: "loose",
          fontFamily: "Arial, sans-serif",
          fontSize: 16,
          htmlLabels: false,
          deterministicIds: true,
          deterministicIdSeed: "mermaid-pet-store",
          maxTextSize: 90000,
          maxEdges: 500,
          flowchart: {
            useMaxWidth: true,
            htmlLabels: false,
            curve: "linear",
            diagramPadding: 8,
            nodeSpacing: 50,
            rankSpacing: 50,
            padding: 15,
          },
          sequence: {
            useMaxWidth: true,
            htmlLabels: false,
            diagramMarginX: 50,
            diagramMarginY: 10,
            actorMargin: 50,
            width: 150,
            height: 65,
            boxMargin: 10,
            boxTextMargin: 5,
            noteMargin: 10,
            messageMargin: 35,
            mirrorActors: true,
            bottomMarginAdj: 1,
            rightAngles: false,
            showSequenceNumbers: false,
            wrap: true,
          },
          gantt: {
            useMaxWidth: true,
            leftPadding: 75,
            gridLineStartPadding: 35,
            fontSize: 11,
            fontFamily: "Arial, sans-serif",
            sectionFontSize: 24,
            numberSectionStyles: 4,
          },
          er: {
            useMaxWidth: true,
            diagramPadding: 20,
            layoutDirection: "TB",
            minEntityWidth: 100,
            minEntityHeight: 75,
            entityPadding: 15,
            stroke: "gray",
            fill: "honeydew",
            fontSize: 12,
          },
          journey: {
            useMaxWidth: true,
            diagramMarginX: 50,
            diagramMarginY: 10,
            leftMargin: 150,
            width: 150,
            height: 50,
            boxMargin: 10,
            boxTextMargin: 5,
            noteMargin: 10,
            messageMargin: 35,
            bottomMarginAdj: 1,
          },
          pie: {
            useMaxWidth: true,
            textPosition: 0.75,
          },
          quadrantChart: {
            useMaxWidth: true,
            chartWidth: 500,
            chartHeight: 500,
          },
          xyChart: {
            useMaxWidth: true,
            width: 700,
            height: 500,
          },
          gitGraph: {
            useMaxWidth: true,
            diagramPadding: 8,
            nodeLabel: {
              width: 75,
              height: 100,
              x: -25,
              y: -8,
            },
          },
          mindmap: {
            useMaxWidth: true,
            padding: 10,
            maxNodeSizeX: 200,
            maxNodeSizeY: 100,
          },
          timeline: {
            useMaxWidth: true,
            diagramMarginX: 50,
            diagramMarginY: 10,
            leftMargin: 150,
            width: 150,
            height: 50,
            padding: 5,
          },
          sankey: {
            useMaxWidth: true,
            width: 600,
            height: 400,
            linkColor: "gradient",
            nodeAlignment: "justify",
          },
          block: {
            useMaxWidth: true,
            padding: 8,
          },
        });

        this.mermaidInitialized = true;
        console.log("Mermaid initialized successfully with safe configuration");
      } catch (error) {
        console.error("Failed to initialize Mermaid:", error);
        this.mermaidInitialized = false;
      }
    }
  }

  private async renderDiagram() {
    if (
      !this.diagramDefinition.trim() ||
      !window.mermaid ||
      !this.mermaidLoaded
    ) {
      return;
    }

    try {
      const element = this.mermaidDiv.nativeElement;

      if (!element || !element.parentElement) {
        console.warn("Mermaid container not properly attached to DOM");
        return;
      }

      element.innerHTML = "";

      const timestamp = Date.now();
      const randomId = Math.random().toString(36).substr(2, 12);
      const instanceId = `${
        this.title?.replace(/[^a-zA-Z0-9]/g, "-").toLowerCase() || "diagram"
      }`;
      const componentId = `comp-${MermaidDiagramComponent.renderCounter++}`;
      const diagramId = `mermaid-${componentId}-${instanceId}-${timestamp}-${randomId}`;

      const diagramContainer = document.createElement("div");
      diagramContainer.id = diagramId;
      diagramContainer.className = "mermaid-isolated";
      diagramContainer.setAttribute("data-diagram-instance", diagramId);
      diagramContainer.style.cssText = `
        width: 100%;
        height: auto;
        display: block;
        text-align: center;
        margin: 0 auto;
        background: transparent;
        min-height: 100px;
        position: relative;
        isolation: isolate;
        contain: layout style paint;
      `;

      diagramContainer.textContent = this.diagramDefinition;
      element.appendChild(diagramContainer);

      element.style.isolation = "isolate";
      element.style.contain = "layout style paint";

      await new Promise((resolve) => setTimeout(resolve, 150));

      const existingContainer = document.getElementById(diagramId);
      if (!existingContainer || existingContainer !== diagramContainer) {
        console.warn("Container isolation failed, recreating...");
        element.innerHTML = "";
        const newContainer = document.createElement("div");
        newContainer.id = diagramId + "-retry";
        newContainer.className = "mermaid-isolated";
        newContainer.setAttribute(
          "data-diagram-instance",
          diagramId + "-retry"
        );
        newContainer.style.cssText = diagramContainer.style.cssText;
        newContainer.textContent = this.diagramDefinition;
        element.appendChild(newContainer);

        await new Promise((resolve) => setTimeout(resolve, 100));
      }

      const finalContainer = element.querySelector(
        ".mermaid-isolated"
      ) as HTMLElement;
      if (!finalContainer) {
        throw new Error("Failed to create isolated container");
      }

      try {
        if (window.mermaid.mermaidAPI && window.mermaid.mermaidAPI.reset) {
          window.mermaid.mermaidAPI.reset();
        }

        window.mermaid.initialize({
          startOnLoad: false,
          theme: this.theme,
          securityLevel: "loose",
          fontFamily: "Arial, sans-serif",
          fontSize: 16,
          htmlLabels: false,
          deterministicIds: false,
          maxTextSize: 90000,
          maxEdges: 500,
          flowchart: {
            useMaxWidth: true,
            htmlLabels: false,
            curve: "linear",
            diagramPadding: 8,
            nodeSpacing: 50,
            rankSpacing: 50,
            padding: 15,
          },
        });

        const rendererId = `${finalContainer.id}-render-${Date.now()}`;
        const { svg } = await window.mermaid.render(
          rendererId,
          this.diagramDefinition
        );
        finalContainer.innerHTML = svg;
        console.log(
          `Mermaid diagram rendered successfully: ${this.title || "Untitled"}`
        );
      } catch (renderError) {
        console.warn(
          "Mermaid render method failed, trying init method:",
          renderError
        );

        try {
          await window.mermaid.init(undefined, finalContainer);
          console.log(
            `Mermaid diagram rendered with init method: ${
              this.title || "Untitled"
            }`
          );
        } catch (altError) {
          console.error("All rendering methods failed:", altError);
          throw altError;
        }
      }
    } catch (error) {
      console.error("Error rendering Mermaid diagram:", error);
      const errorMessage =
        error instanceof Error ? error.message : String(error);
      this.showErrorMessage(`Error rendering diagram: ${errorMessage}`);
    }
  }

  private showErrorMessage(message: string) {
    this.mermaidDiv.nativeElement.innerHTML = `<div class="error-message">
        <p>❌ ${message}</p>
        <details>
          <summary>Troubleshooting</summary>
          <p>Please check your internet connection and try refreshing the page.</p>
        </details>
      </div>`;
  }

  async downloadSVG() {
    try {
      const svgElement = this.mermaidDiv.nativeElement.querySelector("svg");
      if (svgElement) {
        const svgData = new XMLSerializer().serializeToString(svgElement);
        const svgBlob = new Blob([svgData], {
          type: "image/svg+xml;charset=utf-8",
        });
        const svgUrl = URL.createObjectURL(svgBlob);

        const downloadLink = document.createElement("a");
        downloadLink.href = svgUrl;
        downloadLink.download = `${
          this.title.replace(/[^a-z0-9]/gi, "_").toLowerCase() || "diagram"
        }.svg`;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
        URL.revokeObjectURL(svgUrl);
      }
    } catch (error) {
      console.error("Error downloading SVG:", error);
      alert("Failed to download diagram. Please try again.");
    }
  }

  toggleFullscreen() {
    this.isFullscreen = !this.isFullscreen;
    const container =
      this.mermaidDiv.nativeElement.closest(".mermaid-container");

    if (this.isFullscreen) {
      container.classList.add("fullscreen");
      document.body.style.overflow = "hidden";
    } else {
      container.classList.remove("fullscreen");
      document.body.style.overflow = "auto";
    }
  }

  async copyToClipboard() {
    try {
      await navigator.clipboard.writeText(this.diagramDefinition);
      console.log("Diagram definition copied to clipboard");
    } catch (error) {
      console.error("Failed to copy to clipboard:", error);
      const textArea = document.createElement("textarea");
      textArea.value = this.diagramDefinition;
      document.body.appendChild(textArea);
      textArea.select();
      document.execCommand("copy");
      document.body.removeChild(textArea);
    }
  }
}
