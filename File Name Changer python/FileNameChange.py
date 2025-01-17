import os
import re
import tkinter as tk
from tkinter import filedialog, ttk


class FileRenamerApp:
    def __init__(self, root):
        self.root = root
        self.root.title("File Renamer")
        self.root.geometry("600x400")
        
        # Apply theme
        style = ttk.Style()
        style.theme_use("clam")  # Choose a modern theme
        
        # Customize progress bar color to blue
        style.configure("blue.Horizontal.TProgressbar", troughcolor="white", background="#0078D7", thickness=10)
        
        # Frame for folder selection
        folder_frame = ttk.Frame(root, padding=(5, 5, 5, 5))
        folder_frame.pack(fill="x")
        
        ttk.Label(folder_frame, text="Folder:").grid(row=0, column=0, sticky="e")
        self.folder_entry = ttk.Entry(folder_frame, width=40, state="readonly")
        self.folder_entry.grid(row=0, column=1, padx=5)
        ttk.Button(folder_frame, text="Browse", command=self.browse_folder).grid(row=0, column=2, padx=5)
        
        # Frame for new season input and buttons
        action_frame = ttk.Frame(root, padding=(5, 5, 5, 5))
        action_frame.pack(fill="x")
        
        ttk.Label(action_frame, text="Season:").grid(row=0, column=0, sticky="e")
        self.season_entry = ttk.Entry(action_frame, width=5)
        self.season_entry.grid(row=0, column=1, padx=5)
        self.season_entry.bind("<Return>", lambda event: self.preview_renames())
        
        ttk.Button(action_frame, text="Preview", command=self.preview_renames).grid(row=0, column=2, padx=5)
        ttk.Button(action_frame, text="Apply", command=self.apply_changes).grid(row=0, column=3, padx=5)
        
        # Treeview for preview
        self.tree = ttk.Treeview(root, columns=("Old Name", "New Name"), show="headings", height=10)
        self.tree.heading("Old Name", text="Old Name")
        self.tree.heading("New Name", text="New Name")
        self.tree.column("Old Name", anchor="w", width=250)
        self.tree.column("New Name", anchor="w", width=250)
        self.tree.pack(fill="both", expand=True, padx=10, pady=10)
        
        # Progress bar with custom blue style
        self.progress = ttk.Progressbar(root, orient="horizontal", length=100, mode="determinate", style="blue.Horizontal.TProgressbar")
        self.progress.pack(fill="x", padx=10, pady=5)
        
        # Status bar
        self.status_label = ttk.Label(root, text="Ready", anchor="w")
        self.status_label.pack(fill="x", side="bottom", padx=5)
        
        # Initialize variables
        self.rename_mapping = {}
        self.folder_path = ""
        
    def browse_folder(self):
        """Browse and select a folder."""
        self.folder_path = filedialog.askdirectory()
        if self.folder_path:
            self.folder_entry.config(state="normal")
            self.folder_entry.delete(0, "end")
            self.folder_entry.insert(0, self.folder_path)
            self.folder_entry.config(state="readonly")
            self.populate_old_names()
    
    def populate_old_names(self):
        """Populate the tree with the old file names."""
        pattern = re.compile(r"(.*?)(s)(\d{2})(e)(\d{2})(.*)", re.IGNORECASE)
        for item in self.tree.get_children():
            self.tree.delete(item)
        
        file_count = 0
        max_length = 0
        for filename in os.listdir(self.folder_path):
            if pattern.search(filename) and filename.lower().endswith((".mp4", ".txt", ".mkv")):
                self.tree.insert("", "end", values=(filename, ""))
                file_count += 1
                max_length = max(max_length, len(filename))
        
        # Update status bar
        self.status_label.config(text=f"{file_count} files loaded")
        
    def preview_renames(self):
        """Generate a preview of the file renames."""
        if not self.folder_path:
            self.status_label.config(text="Error: No folder selected")
            return
        try:
            new_season = int(self.season_entry.get())
        except ValueError:
            self.status_label.config(text="Error: Invalid season number")
            return
        
        self.rename_mapping = self.generate_rename_mapping(new_season)
        if not self.rename_mapping:
            self.status_label.config(text="No matching files found")
            return
        
        for item in self.tree.get_children():
            old_name = self.tree.item(item, "values")[0]
            new_name = self.rename_mapping.get(old_name, "")
            self.tree.item(item, values=(old_name, new_name))
        
        self.status_label.config(text="Preview completed")
    
    def generate_rename_mapping(self, new_season):
        """Generate a mapping of old filenames to new filenames."""
        pattern = re.compile(r"(.*?)(s)(\d{2})(e)(\d{2})(.*)", re.IGNORECASE)
        rename_mapping = {}
        
        files = [
            (filename, pattern.search(filename))
            for filename in os.listdir(self.folder_path)
            if pattern.search(filename) and filename.lower().endswith((".mp4", ".txt", ".mkv"))
        ]
        files.sort(key=lambda x: int(x[1].group(5)))
        
        for i, (filename, match) in enumerate(files, start=1):
            new_episode = i
            prefix = match.group(1)
            suffix = match.group(6)
            new_filename = f"{prefix}s{new_season:02d}e{new_episode:02d}{suffix}"
            rename_mapping[filename] = new_filename
        
        return rename_mapping
    
    def apply_changes(self):
        """Apply the renaming changes."""
        if not self.rename_mapping:
            self.status_label.config(text="Error: No changes to apply")
            return
        
        self.progress["maximum"] = len(self.rename_mapping)
        for i, (old_name, new_name) in enumerate(self.rename_mapping.items(), start=1):
            old_path = os.path.join(self.folder_path, old_name)
            new_path = os.path.join(self.folder_path, new_name)
            os.rename(old_path, new_path)
            
            # Update progress bar
            self.progress["value"] = i
            self.root.update_idletasks()
        
        self.status_label.config(text="Renaming completed")
        self.rename_mapping.clear()
        for item in self.tree.get_children():
            self.tree.delete(item)
        self.populate_old_names()