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
  HostListener,
  ChangeDetectorRef,
  ChangeDetectionStrategy,
  ViewEncapsulation,
} from "@angular/core";
import { CommonModule, isPlatformBrowser } from "@angular/common";
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

declare global {
  interface Window {
    mermaid: any;
  }
}

@Component({
  selector: "app-mermaid-diagram",
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatTooltipModule],
  templateUrl: "./mermaid-diagram.component.html",
  styleUrls: ["./mermaid-diagram.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None, // Allow fullscreen styles to apply globally
})
export class MermaidDiagramComponent
  implements OnInit, AfterViewInit, OnDestroy, OnChanges
{
  @ViewChild("mermaidDiv", { static: true }) mermaidDiv!: ElementRef;
  @ViewChild("containerDiv", { static: true }) containerDiv!: ElementRef;

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
  private isTogglingFullscreen: boolean = false;
  private originalParent: HTMLElement | null = null;
  private originalNextSibling: Node | null = null;
  
  // Zoom and pan properties
  zoomLevel: number = 1;
  panX: number = 0;
  panY: number = 0;
  isPanning: boolean = false;
  private startX: number = 0;
  private startY: number = 0;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.diagramId = "mermaid-" + Math.random().toString(36).substr(2, 9);
    /* console.log('MermaidDiagramComponent initialized:', {
      title: this.title,
      showActions: this.showActions,
      diagramId: this.diagramId
    }); */
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
      // Cleanup without triggering toggle
      this.isFullscreen = false;
      document.body.style.overflow = "";
      if (this.containerDiv && this.containerDiv.nativeElement) {
        const container = this.containerDiv.nativeElement as HTMLElement;
        container.classList.remove("fullscreen");
      }
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
        //console.log("Mermaid initialized successfully with safe configuration");
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

      // Don't set isolation/contain inline styles - they interfere with fullscreen
      // element.style.isolation = "isolate";
      // element.style.contain = "layout style paint";

      await new Promise((resolve) => setTimeout(resolve, 50));

      const finalContainer = diagramContainer;

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
        /* console.log(
          `Mermaid diagram rendered successfully: ${this.title || "Untitled"}` 
        );*/
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
    // Prevent multiple simultaneous toggles
    if (this.isTogglingFullscreen) {
      return;
    }

    this.isTogglingFullscreen = true;
    console.log('Toggling fullscreen from:', this.isFullscreen, 'to:', !this.isFullscreen);
    
    try {
      // Toggle the state
      this.isFullscreen = !this.isFullscreen;
      
      // Get the container element
      if (!this.containerDiv || !this.containerDiv.nativeElement) {
        console.error('Container element not found');
        this.isFullscreen = !this.isFullscreen;
        this.isTogglingFullscreen = false;
        return;
      }

      const container = this.containerDiv.nativeElement as HTMLElement;
      
      // Apply or remove fullscreen styles
      if (this.isFullscreen) {
        // Save original position in DOM
        this.originalParent = container.parentElement;
        this.originalNextSibling = container.nextSibling;
        
        // Move to body to break out of parent constraints
        document.body.appendChild(container);
        
        container.classList.add("fullscreen");
        document.body.style.overflow = "hidden";
        
        // Force inline styles to ensure fullscreen displays
        container.style.position = 'fixed';
        container.style.top = '0';
        container.style.left = '0';
        container.style.width = '100vw';
        container.style.height = '100vh';
        container.style.zIndex = '99999';
        container.style.background = '#ffffff';
        container.style.margin = '0';
        container.style.padding = '0';
        container.style.border = 'none';
        container.style.borderRadius = '0';
        container.style.boxShadow = 'none';
        container.style.overflow = 'auto';
        
        console.log('Fullscreen activated - moved to body');
      } else {
        // Reset zoom and pan when exiting fullscreen
        this.resetZoom();
        
        container.classList.remove("fullscreen");
        document.body.style.overflow = "";
        
        // Remove inline styles to restore normal view
        container.style.position = '';
        container.style.top = '';
        container.style.left = '';
        container.style.width = '';
        container.style.height = '';
        container.style.zIndex = '';
        container.style.background = '';
        container.style.margin = '';
        container.style.padding = '';
        container.style.border = '';
        container.style.borderRadius = '';
        container.style.boxShadow = '';
        container.style.overflow = '';
        
        // Move back to original position in DOM
        if (this.originalParent) {
          if (this.originalNextSibling) {
            this.originalParent.insertBefore(container, this.originalNextSibling);
          } else {
            this.originalParent.appendChild(container);
          }
        }
        
        console.log('Fullscreen deactivated - moved back to original position');
      }
      
      // Mark for check with OnPush strategy
      this.cdr.markForCheck();
      
      console.log('Fullscreen toggled successfully');
      
    } catch (error) {
      console.error('Error in toggleFullscreen:', error);
      this.isFullscreen = !this.isFullscreen;
    }
    
    // Always reset the lock
    this.isTogglingFullscreen = false;
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscapeKey(event: KeyboardEvent) {
    if (this.isFullscreen) {
      this.toggleFullscreen();
      event.preventDefault();
    }
  }

  onFullscreenBackdropClick(event: MouseEvent) {
    // Only close if clicking directly on the container (not its children)
    if (this.isFullscreen && event.target === event.currentTarget) {
      this.toggleFullscreen();
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

  // Zoom and Pan functionality
  onWheel(event: WheelEvent) {
    if (!this.isFullscreen) return;
    
    event.preventDefault();
    const delta = event.deltaY > 0 ? -0.1 : 0.1;
    this.zoomLevel = Math.max(0.5, Math.min(5, this.zoomLevel + delta));
    this.applyTransform();
  }

  onMouseDown(event: MouseEvent) {
    if (!this.isFullscreen) return;
    
    this.isPanning = true;
    this.startX = event.clientX - this.panX;
    this.startY = event.clientY - this.panY;
    event.preventDefault();
  }

  @HostListener('document:mousemove', ['$event'])
  onMouseMove(event: MouseEvent) {
    if (!this.isPanning || !this.isFullscreen) return;
    
    this.panX = event.clientX - this.startX;
    this.panY = event.clientY - this.startY;
    this.applyTransform();
  }

  @HostListener('document:mouseup')
  onMouseUp() {
    this.isPanning = false;
  }

  zoomIn() {
    this.zoomLevel = Math.min(5, this.zoomLevel + 0.2);
    this.applyTransform();
  }

  zoomOut() {
    this.zoomLevel = Math.max(0.5, this.zoomLevel - 0.2);
    this.applyTransform();
  }

  resetZoom() {
    this.zoomLevel = 1;
    this.panX = 0;
    this.panY = 0;
    this.applyTransform();
  }

  private applyTransform() {
    if (!this.mermaidDiv?.nativeElement) return;
    
    const diagramDiv = this.mermaidDiv.nativeElement as HTMLElement;
    diagramDiv.style.transform = `translate(${this.panX}px, ${this.panY}px) scale(${this.zoomLevel})`;
    diagramDiv.style.transformOrigin = 'center center';
    diagramDiv.style.transition = 'transform 0.1s ease-out';
    
    this.cdr.markForCheck();
  }
}
