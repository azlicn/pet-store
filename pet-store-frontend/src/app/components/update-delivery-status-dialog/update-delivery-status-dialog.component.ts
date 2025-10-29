import { Component, Inject } from "@angular/core";
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from "@angular/material/dialog";
import { FormControl, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { DeliveryStatus } from "../../models/delivery.model";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatButtonModule } from "@angular/material/button";
import { MatSelectModule } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";

@Component({
  selector: "app-update-delivery-status-dialog",
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
  MatButtonModule,
  MatSelectModule,
  MatInputModule,
  MatDatepickerModule,
  MatNativeDateModule
  ],
  templateUrl: "./update-delivery-status-dialog.component.html",
  styleUrls: ["./update-delivery-status-dialog.component.scss"],
})
export class UpdateDeliveryStatusDialogComponent {
  deliveryStatuses = Object.values(DeliveryStatus);
  statusControl = new FormControl(this.data.status);
  dateControl = new FormControl(this.data.date);

  constructor(
    public dialogRef: MatDialogRef<UpdateDeliveryStatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { status: DeliveryStatus; date: string }
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    this.dialogRef.close({
      status: this.statusControl.value,
      date: this.dateControl.value,
    });
  }
}
