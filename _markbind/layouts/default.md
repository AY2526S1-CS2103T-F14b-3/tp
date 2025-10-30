<head-bottom>
  <link rel="stylesheet" href="{{baseUrl}}/stylesheets/main.css">
  <!-- Add other global head assets here if needed -->
</head-bottom>

<div id="page-container">
  <header>
    <md>
      <include src="../navigation.md" />
    </md>
  </header>

  <!-- Custom fixed sidebar (desktop) -->
  <div id="custom-sidebar">
    <include src="../sideNav.md"/>
    <!-- Spacer to avoid overlap with sticky top bar -->
    <div class="sidebar-spacer" aria-hidden="true"></div>
  </div>

  <div id="content">
    {{ content }}
  </div>
</div>
